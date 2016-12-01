/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
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
        params.add("uri", uri);
        SolrParams toSolrParams = SolrParams.toSolrParams(params);
        QueryResponse query = Solr.query(toSolrParams);
        return query.getResults().size() != 0;

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
