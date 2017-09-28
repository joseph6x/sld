/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.json.simple.JSONObject;

/**
 *
 * @author cedia
 */
public class test {

    public static void main(String[] args) throws Exception {
        SolrConnection instance = SolrConnection.getInstance();
        List<String[]> Find = instance.Find(new String[]{"endpoint"}, new String[]{"cepalstat"}, new boolean[]{true}, new String[]{"uri"}, true, -1, true);
        System.out.println(Find.get(0)[0]);
        
//        JSONObject OneResult = new JSONObject();
//        
//        OneResult.put("algo", "áéćááá");
//        
//        Cache.getInstance().put("a", OneResult.toJSONString());
//        String get = Cache.getInstance().get("a");
//        
//        JsonObject parse = JSON.parse(get);
//        
//        String value = parse.get("algo").getAsString().value();
//        
//        System.out.println(value);
//        
        //System.out.println(YandexDictionary.Syn("cat", 10));
//        SPARQL sp = new SPARQL();
//
//        List<String> SimpleQueryString = sp.SimpleQueryString("select ?d { ?c <http://purl.org/dc/terms/date> ?d .  }", "http://10.0.29.117:8891/biblioguias/sparql", "d");
//
//        for (String st : SimpleQueryString) {
//
//            Date tryParse = LinksFilesUtiles.tryParse(st);
//
//            //if (tryParse == null) {
//            System.out.println(st + "<->" + tryParse);
//            //}
//
//        }
        //JsonObject parse = JSON.parse("{a:[\"a\",\"b\"]}");
        //JsonArray asArray = parse.get("a").getAsArray();
//        SolrConnection instance = SolrConnection.getInstance();
//
//        List<String[]> Find = instance.Find(new String[]{"finalText","pathText"}, new String[]{"caribe","social"}, new String[]{"uri"},false, 10);
//        
//        for (String[] a: Find){
//            System.out.println(Arrays.toString(a));
//        }
//
        //HarvestingDemon D1 = new HarvestingDemon();
        //D1.start();
        //http://localhost:8080/cepalstat/indicador/Indicador_1973
        // SolrConnection instance = SolrConnection.getInstance();
        // List<String> FindLinks = LinksDiscovery.FindLinks("http://localhost:8080/cepalstat/indicador/Indicador_2947");
        //SolrConnection instance = SolrConnection.getInstance();
        // List<String> FindLinks2 = instance.Find2("finalText", "(" + ClientUtils.escapeQueryChars("'pobreza' 'poverty'") + ")", "endpoint", "cepalstat", 10);
        //System.out.println(FindLinks2);
        //SPARQL sp = new SPARQL();
        //String Count = "select (count(*) as ?c) where { ?r a <http://purl.org/ontology/bibo/Document> }";
        //List<RDFNode> SimpleQuery = sp.SimpleQuery(Count, "http://190.15.141.102:8891/myservice/sparql", "c");
        //RDFNode get = SimpleQuery.get(0);
        //int aInt = get.asLiteral().getInt();
        //boolean s= SolrConnection.getInstance().exists("http://190.15.141.66:8899/ucuenca/recurso/16606");
        //System.out.println(CorticalAPI.Similarity("", ""));
        //HarvestingDemon myThread = new HarvestingDemon();
        //  myThread.start();
        //SynonymsDemon myThread = new SynonymsDemon();
        //myThread.start();
        //  CorticalDemon myThread = new CorticalDemon();
        //  myThread.start();
        //LinksDemon myThread = new LinksDemon();
        // myThread.start();
    }

}
