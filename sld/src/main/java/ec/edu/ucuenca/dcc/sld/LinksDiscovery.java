/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import com.hp.hpl.jena.rdf.model.RDFNode;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author cedia
 */
public class LinksDiscovery {

    private String Endpoint;

    private String MainClass;

    private String Query;

    private String Name;

    public LinksDiscovery(String Endpoint, String MainClass, String Query, String Name) {
        this.Endpoint = Endpoint;
        this.MainClass = MainClass;
        this.Query = Query;
        this.Name = Name;
    }

    public List<String> FindLinks(String uri) throws SolrServerException, IOException {
        SolrConnection instance = SolrConnection.getInstance();
        String[] FindOne = instance.FindOne("uri", uri, "originalText", "uri", "finalText");
        if (FindOne == null) {
            return new ArrayList<>();
        }
        String txt = FindOne[2];

        txt = txt.replace(",", "").trim();
        List<String> Find = new ArrayList<>();
        if (txt.compareTo("") != 0) {
            Find = instance.Find("finalText", "(" + txt + ")", ConfigInfo.getInstance().getConfig().get("LinksThreshold").getAsNumber().value().doubleValue());
        }

        return Find;
    }

    public void Discovery() throws SolrServerException, IOException {

        String value = ConfigInfo.getInstance().getConfig().get("Output").getAsString().value();

        String filename = value + Name + ".ttl";
        
        PrintWriter writer = new PrintWriter(filename);
        writer.print("");
        writer.close();

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

                List<String> FindLinks = FindLinks(uri);

                AddLinks(uri, FindLinks);

            }
        }
    }

    public void AddLinks(String uri, List<String> links) {

        ConfigInfo instance = ConfigInfo.getInstance();
        String value = instance.getConfig().get("Output").getAsString().value();

        try {
            String filename = value + Name + ".ttl";
            FileWriter fw = new FileWriter(filename, true); //the true will append the new data
            for (String uri2 : links) {
                if (uri.trim().compareTo(uri2.trim()) != 0) {
                    fw.write("<" + uri + "> <http://www.w3.org/2000/01/rdf-schema#seeAlso> <" + uri2 + ">\n");//appends the string to the file
                }

            }

            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

    }

}
