/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author cedia
 */
public class test {

    public static void main(String[] args) throws Exception {
        //http://localhost:8080/cepalstat/indicador/Indicador_1973
        
        SolrConnection instance = SolrConnection.getInstance();
        
        
        System.out.println(instance.Find2("finalText", "(" + "Ecuador Quito" + ")", "endpoint", "repositorio", 15));
        
        
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
