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
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    public void insert(String[] var, Object[] val) throws SolrServerException, IOException, Exception {
        if (var.length == val.length) {
        } else {
            throw new Exception("Var != Val != Quo");
        }

        SolrInputDocument document = new SolrInputDocument();
        for (int i = 0; i < var.length; i++) {
            document.addField(var[i], val[i]);
        }

        UpdateResponse add = Solr.add(document);
        Solr.commit();
    }

    public List<String[]> FindMod(String endpoint, String pquery, int limit, String mm) throws SolrServerException, IOException {

        String[] out = {"uri"};
        int current = 0;
        List<String[]> lsResults = new ArrayList<>();
        NamedList params = new NamedList();

        String newquery = "+(" + pquery + ") +(endpoint:\"" + endpoint + "\")";
        System.out.println("LOG_Solr_" + newquery);
        params.add("q", newquery);
        params.add("fl", "*,score");
        params.add("start", current + "");
        params.add("defType", "edismax");
        //params.add("mm", ""+mm);
        params.add("qf", "finalText");
        while (true) {
            params.setVal(2, current + "");
            SolrParams toSolrParams = SolrParams.toSolrParams(params);
            QueryResponse query = Solr.query(toSolrParams, SolrRequest.METHOD.POST);
            SolrDocumentList results = query.getResults();
            if (!query.getResults().isEmpty()) {
                boolean end = false;
                for (int i = 0; i < results.size(); i++) {
                    String txt[] = new String[out.length];
                    SolrDocument get = results.get(i);
                    current++;
                    for (int ix = 0; ix < out.length; ix++) {
                        txt[ix] = get.getFieldValue(out[ix]).toString();
                    }
                    if (Double.parseDouble(get.getFieldValue("score").toString()) > 1.0) {
                        lsResults.add(txt);
                    }
                    if (limit != -1 && current >= limit) {
                        end = true;
                        break;
                    }
                }
                if (end) {
                    break;
                }
            } else {
                break;
            }
        }

        return lsResults;

    }

    public List<String[]> Find(String[] var, String[] val, boolean[] quo, String[] out, boolean and, int limit, boolean firstAnd) throws SolrServerException, IOException, Exception {

        int current = 0;
        List<String[]> lsResults = new ArrayList<>();

        if (var.length == val.length && val.length == quo.length) {
        } else {
            throw new Exception("Var != Val != Quo");
        }
        if (var.length <= 1 && firstAnd) {
            System.err.println("Ignoring firstAnd..");
            firstAnd = false;
        }

        NamedList params = new NamedList();
        String qry = "";
        for (int i = 0; i < var.length; i++) {

            qry += var[i] + ":" + "" + (quo[i] ? "\"" : "") + val[i] + (quo[i] ? "\"" : "") + "";
            if (i != var.length - 1) {
                if (and || firstAnd && i == 0) {
                    qry += " AND ";
                    if (firstAnd && i == 0) {
                        qry += " ( ";
                    }
                } else {
                    qry += " OR ";
                }
            } else {
                if (firstAnd) {
                    qry += " ) ";
                }
            }
        }
        System.out.println("LOG_Solr_" + qry);
        params.add("q", qry);
        params.add("fl", "*,score");
        params.add("start", current + "");
        while (true) {
            params.setVal(2, current + "");
            SolrParams toSolrParams = SolrParams.toSolrParams(params);
            QueryResponse query = Solr.query(toSolrParams, SolrRequest.METHOD.POST);
            SolrDocumentList results = query.getResults();
            if (!query.getResults().isEmpty()) {
                boolean end = false;
                for (int i = 0; i < results.size(); i++) {
                    String txt[] = new String[out.length];
                    SolrDocument get = results.get(i);
                    current++;
                    for (int ix = 0; ix < out.length; ix++) {
                        txt[ix] = get.getFieldValue(out[ix]).toString();
                    }
                    lsResults.add(txt);
                    if (limit != -1 && current >= limit) {
                        end = true;
                        break;
                    }
                }
                if (end) {
                    break;
                }
            } else {
                break;
            }
        }

        return lsResults;
    }

    public void remove(String[] var, String[] val, boolean[] quo, boolean and) throws SolrServerException, IOException, Exception {

        if (var.length == val.length && val.length == quo.length) {
        } else {
            throw new Exception("Var != Val != Quo");
        }

        String qry = "";
        for (int i = 0; i < var.length; i++) {
            qry += var[i] + ":" + "" + (quo[i] ? "\"" : "") + val[i] + (quo[i] ? "\"" : "") + "";
            if (i != var.length - 1) {
                if (and) {
                    qry += " AND ";
                } else {
                    qry += " OR ";
                }
            }
        }
        Solr.deleteByQuery(qry);
        Solr.commit();

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
