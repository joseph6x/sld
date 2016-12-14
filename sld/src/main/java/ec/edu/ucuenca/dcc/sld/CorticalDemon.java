/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author cedia
 */
public class CorticalDemon extends Thread {

    @Override
    public void run() {

        // 58ef39e0-b91a-11e6-a057-97f4c970893c
        for (;;) {

            try {
                SolrConnection instance = SolrConnection.getInstance();
                String[] FindOne;
                FindOne = instance.FindOne("state", "1", "originalText", "uri", "originalTextSyn", "endpoint");
                if (FindOne == null) {
                    Thread.sleep(1000*60*60*3);
                } else {
                    boolean upd = true;
                    String uri = FindOne[0];
                    String orgtxt = FindOne[1];
                    String orgtxtsyn = FindOne[2];
                    String ep = FindOne[3];

                    String fntxt = "";

                    ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(orgtxtsyn.split(",")));

                    arrayList.removeAll(Arrays.asList("", null));

                    for (String s : arrayList) {
                        if (s.trim().compareTo("") == 0) {
                            continue;
                        }

                        int d = 0;
                        d = CorticalAPI.SemanticDistance(s, orgtxt.replace(',', ' '));
                        if (d > ConfigInfo.getInstance().getConfig().get("CorticalThreshold").getAsNumber().value().doubleValue()) {
                            fntxt += s + ", ";
                        }
                    }

                    //http://api.cortical.io:80/rest/compare?retina_name=en_associative
                    if (upd) {
                        SolrConnection instance1 = SolrConnection.getInstance();
                        SolrClient solr = instance1.getSolr();
                        solr.deleteByQuery("uri:\"" + uri + "\"");
                        solr.commit();
                        SolrInputDocument document = new SolrInputDocument();
                        document.addField("uri", uri);
                        document.addField("originalText", orgtxt);
                        document.addField("originalTextSyn", orgtxtsyn);
                        document.addField("finalText", orgtxt + " " + fntxt);
                        document.addField("state", 2);
                        document.addField("endpoint", ep);
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
