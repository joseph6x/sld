/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;

/**
 *
 * @author cedia
 */
public class SolrConnection {

    private SolrClient Solr;

    private SolrConnection() {

        ConfigInfo instance = ConfigInfo.getInstance();
        String value = instance.getConfig().get("SolrServer").getAsString().value();
        String urlString = value;
        SolrClient solr = new HttpSolrClient.Builder(urlString).build();
        Solr = solr;

    }

    public boolean exists(String uri) throws SolrServerException, IOException {

        NamedList params = new NamedList();
        params.add("q", "uri:" + "\"" + uri + "\"");
        SolrParams toSolrParams = SolrParams.toSolrParams(params);
        QueryResponse query = Solr.query(toSolrParams, SolrRequest.METHOD.POST);

        return !query.getResults().isEmpty();

    }

    public List<String> Find2(String var, String val, String var2, String val2, int limit) throws SolrServerException, IOException {

        int current = 0;

        List<String> ls = new ArrayList<>();
        NamedList params = new NamedList();
        params.add("q", var + ":" + "" + val + "\nAND\n" + var2 + ":" + val2);
        params.add("fl", "*,score");
        params.add("start", current + "");

        while (true) {
            params.setVal(2, current + "");
            SolrParams toSolrParams = SolrParams.toSolrParams(params);
            QueryResponse query = Solr.query(toSolrParams, SolrRequest.METHOD.POST);
            SolrDocumentList results = query.getResults();
            if (!query.getResults().isEmpty()) {
                for (int i = 0; i < results.size() && current < limit; i++) {
                    SolrDocument get = results.get(i);
                    //Object fieldValue = get.getFieldValue("score");
                    Object fieldValue1 = get.getFieldValue("uri");
                    //Object fieldValueEP = get.getFieldValue("endpoint");
                    //double parseDouble = Double.parseDouble(fieldValue + "");
                    ls.add(fieldValue1 + "");
                    current += 1;
                }
                if (!(current < limit)) {
                    break;
                }

            } else {
                break;
            }
        }

        return ls;
    }

    public List<String> Find(String var, String val, double minScore) throws SolrServerException, IOException {

        List<String> ls = new ArrayList<>();

        NamedList params = new NamedList();
        params.add("q", var + ":" + "" + val + "");
        params.add("fl", "*,score");

        SolrParams toSolrParams = SolrParams.toSolrParams(params);

        //System.out.println(toSolrParams.toQueryString());
        QueryResponse query = Solr.query(toSolrParams, SolrRequest.METHOD.POST);
        SolrDocumentList results = query.getResults();
        if (!query.getResults().isEmpty()) {
            for (int i = 0; i < results.size(); i++) {
                SolrDocument get = results.get(i);
                Object fieldValue = get.getFieldValue("score");
                Object fieldValue1 = get.getFieldValue("uri");
                Object fieldValueEP = get.getFieldValue("endpoint");

                double parseDouble = Double.parseDouble(fieldValue + "");
                if (parseDouble >= minScore) {
                    ls.add(fieldValueEP + "|" + fieldValue1 + "");
                } else {
                    break;
                }

            }

        }

        return ls;
    }

    public String[] FindOne(String var, String val, String vals, String uri, String syn, String ep) throws SolrServerException, IOException {
        String txt[] = null;
        NamedList params = new NamedList();
        params.add("q", var + ":" + "\"" + val + "\"");
        params.add("fl", "*,score");
        SolrParams toSolrParams = SolrParams.toSolrParams(params);
        QueryResponse query = Solr.query(toSolrParams, SolrRequest.METHOD.POST);
        SolrDocumentList results = query.getResults();
        if (!query.getResults().isEmpty()) {
            SolrDocument get = results.get(0);
            Object fieldValue = get.getFieldValue(vals);
            Object fieldValue2 = get.getFieldValue(uri);
            Object fieldValue3 = get.getFieldValue(syn);
            Object fieldValue4 = get.getFieldValue(ep);
            txt = new String[]{fieldValue2 + "", fieldValue + "", fieldValue3 + "", fieldValue4 + ""};
        }
        return txt;
    }

    public String[] FindOne2(String var, String val, String vals, String uri, String syn, String ep) throws SolrServerException, IOException {
        String txt[] = null;
        NamedList params = new NamedList();
        params.add("q", var + ":" + "" + val + "");
        params.add("fl", "*,score");
        SolrParams toSolrParams = SolrParams.toSolrParams(params);
        QueryResponse query = Solr.query(toSolrParams, SolrRequest.METHOD.POST);
        SolrDocumentList results = query.getResults();
        if (!query.getResults().isEmpty()) {
            SolrDocument get = results.get(0);
            Object fieldValue = get.getFieldValue(vals);
            Object fieldValue2 = get.getFieldValue(uri);
            Object fieldValue3 = get.getFieldValue(syn);
            Object fieldValue4 = get.getFieldValue(ep);
            txt = new String[]{fieldValue2 + "", fieldValue + "", fieldValue3 + "", fieldValue4 + ""};
        }
        return txt;
    }

    public static SolrConnection getInstance() {
        return SolrConnectionHolder.INSTANCE;
    }

    private static class SolrConnectionHolder {

        private static final SolrConnection INSTANCE = new SolrConnection();
    }

    public SolrClient getSolr() {
        return Solr;
    }

    public void setSolr(SolrClient Solr) {
        this.Solr = Solr;
    }

}
