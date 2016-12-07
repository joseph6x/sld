/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author cedia
 */
public class SynonymsDemon extends Thread {
//dict.1.1.20161202T224400Z.34ac0ab516f65849.e3fe237d6539b089a94dfc9be13825ebca64572f

    @Override
    public void run() {
        for (;;) {

            try {
                SolrConnection instance = SolrConnection.getInstance();
                String[] FindOne = instance.FindOne("state", "0", "originalText", "uri", "originalTextSyn");

                if (FindOne == null) {
                    Thread.sleep(1000*60*60);
                } else {
                    boolean upd = true;
                    String uri = FindOne[0];
                    String orgtxt = FindOne[1];
                    ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(orgtxt.split(",")));
                    arrayList.removeAll(Arrays.asList("", null));

                    String nsyn = "";

                    for (String txt : arrayList) {
                        if (txt.trim().compareTo("") == 0) {
                            continue;
                        }

                        List<String> Syn = YandexDictionary.Syn(txt.trim(), ConfigInfo.getInstance().getConfig().get("SynNumber").getAsNumber().value().intValue());

                        if (Syn != null) {
                            for (String txtx : Syn) {
                                nsyn += txtx + ", ";
                            }

                        } else {
                            upd = false;
                            Thread.sleep(1000*60*60*6);
                            break;
                        }
                    }
                    if (upd) {

                        SolrConnection instance1 = SolrConnection.getInstance();
                        SolrClient solr = instance1.getSolr();
                        solr.deleteByQuery("uri:\"" + uri + "\"");
                        solr.commit();

                        SolrInputDocument document = new SolrInputDocument();
                        document.addField("uri", uri);
                        document.addField("originalText", orgtxt);
                        document.addField("originalTextSyn", nsyn);
                        document.addField("finalText", orgtxt + " " + nsyn);
                        document.addField("state", 1);

                        UpdateResponse add = solr.add(document);
                        solr.commit();

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
        }

    }

}
