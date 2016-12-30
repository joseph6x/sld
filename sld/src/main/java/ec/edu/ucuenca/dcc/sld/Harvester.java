/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import com.hp.hpl.jena.rdf.model.RDFNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author cedia
 */
public class Harvester {

    private String Endpoint;

    private String MainClass;

    private String Query;
    private String Name;

    public Harvester(String Name, String Endpoint, String MainClass, String Query) {
        this.Endpoint = Endpoint;
        this.MainClass = MainClass;
        this.Query = Query;
        this.Name = Name;
    }

    public void Harvest() throws SolrServerException, IOException {
        int bulk = 1000;
        SPARQL sp = new SPARQL();
        String Count = "select (count(*) as ?c) where { ?r a <" + MainClass + "> }";
        String resources = "select ?r where { ?r a <" + MainClass + "> } limit " + bulk;
        List<RDFNode> SimpleQuery = sp.SimpleQuery(Count, Endpoint, "c");
        RDFNode get = SimpleQuery.get(0);
        int aInt = get.asLiteral().getInt();
        for (int i = 0; i < aInt; i += 1000) {
            String qry = resources + " offset " + i;
            List<RDFNode> SimpleQuery1 = sp.SimpleQuery(qry, Endpoint, "r");
            for (RDFNode d : SimpleQuery1) {
                String uri = d.asResource().getURI();
                if (!SolrConnection.getInstance().exists(uri)) {
                    String Query2 = Query.replaceAll("\\|\\?\\|", uri);
                    List<RDFNode> SimpleQuery2 = sp.SimpleQuery(Query2, Endpoint, "d");
                    AddUpdate(uri, SimpleQuery2);
                } else {
                    //System.out.println("Ya existe" + uri);

                }
            }
        }
    }

    public void AddUpdate(String uri, List<RDFNode> ls) throws SolrServerException, IOException {

        //System.out.println(ls);
        SolrClient solr = SolrConnection.getInstance().getSolr();

        String txt = "";

        for (RDFNode a : ls) {
            if (a.isLiteral()) {
                txt += a.asLiteral().getString() + ", ";
            } else {
                List<String> DbpediaLabel = DbpediaLabel(a.asResource().getURI());
                for (int j = 0; j < DbpediaLabel.size(); j++) {
                    txt += DbpediaLabel.get(j) + ", ";
                }

            }
        }

        SolrInputDocument document = new SolrInputDocument();
        document.addField("uri", uri);
        document.addField("originalText", txt);
        document.addField("originalTextSyn", "");
        document.addField("finalText", txt);
        document.addField("state", 0);
        document.addField("endpoint", this.Name);
        //state

        UpdateResponse response = solr.add(document);

        solr.commit();

    }

    public List<String> DbpediaLabel(String uri) {

        //String txt = "";
        //String qry = "select (str(?t) as ?T) where { <" + uri + "> <http://www.w3.org/2000/01/rdf-schema#label> ?t .  filter (lang(?t) = 'en'). } ";
        String qry2 = "select (str(?t) as ?T) where { <" + uri + "> <http://www.w3.org/2000/01/rdf-schema#label> ?t .  } ";
        SPARQL sp = new SPARQL();
        //List<RDFNode> SimpleQuery1 = sp.SimpleQuery(qry, "http://dbpedia.org/sparql", "T");
        List<RDFNode> SimpleQuery2 = sp.SimpleQuery(qry2, "http://dbpedia.org/sparql", "T");
        //List<RDFNode> SimpleQuery3 = sp.SimpleQuery(qry, "http://es.dbpedia.org/sparql", "T");
        List<RDFNode> SimpleQuery4 = sp.SimpleQuery(qry2, "http://es.dbpedia.org/sparql", "T");

        List<RDFNode> SimpleQuery = new ArrayList<>();
        //SimpleQuery.addAll(SimpleQuery1);
        SimpleQuery.addAll(SimpleQuery2);
        //SimpleQuery.addAll(SimpleQuery3);
        SimpleQuery.addAll(SimpleQuery4);

        List<String> ls = new ArrayList<>();

        for (RDFNode a : SimpleQuery) {
            String string = a.asLiteral().getString();
            ls.add(string);
        }

        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.addAll(ls);

        List list = new ArrayList(linkedHashSet);
        return list;
    }

    public String getEndpoint() {
        return Endpoint;
    }

    public void setEndpoint(String Endpoint) {
        this.Endpoint = Endpoint;
    }

    public String getMainClass() {
        return MainClass;
    }

    public void setMainClass(String MainClass) {
        this.MainClass = MainClass;
    }

    public String getQuery() {
        return Query;
    }

    public void setQuery(String Query) {
        this.Query = Query;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

}
