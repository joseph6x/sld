/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

//import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.HashMap;
import java.util.Map;
//import org.apache.solr.client.solrj.util.ClientUtils;

/**
 *
 * @author cedia
 */
public class test {

    public static void main(String[] args) throws Exception {

        //http://ws.geonames.org/searchJSON?q=gualleturo&country=ec&maxRows=1&username=cedia
        Geonames instance = Geonames.getInstance();
        
       
        System.out.println(instance.getLocation("guallejhjhjuro"));
        
        //SNER instance = SNER.getInstance();
        // List<String> GetNamedEntities = instance.GetNamedEntities("La presente investigación es un análisis de las actividades turísticas que demandaron los jubilados estadounidenses residentes en Cuenca-Ecuador en el año 2012. Este trabajo se basó en un trabajo de campo con una muestra de 340 encuestas. La investigación analizó la demanda de los servicios turísticos que son reconocidos como actividades turísticas en el Art. 5 de la Ley de Turismo del Ecuador. La investigación tiene dos partes: la primera busca establecer si los jubilados estadounidenses demandaron servicios turísticos en Cuenca; la segunda parte es una contribución a la discusión del concepto Turismo Residencial. Al final de la investigación se establece que de acuerdo a los resultados, el 30% de jubilados estadounidenses demandaron las actividades de alimentos y bebidas y oferta complementaria tales como centros comerciales, cines y las actividades culturales. Finalmente, se determinó que el 26% de la muestra representó a los turistas residenciales que vivieron en Cuenca.");
        // System.out.println(GetNamedEntities);
        //http://localhost:8080/cepalstat/indicador/Indicador_1973
        // SolrConnection instance = SolrConnection.getInstance();
        // List<String> FindLinks = LinksDiscovery.FindLinks("http://localhost:8080/cepalstat/indicador/Indicador_2947");
        //  SolrConnection instance = SolrConnection.getInstance();
        //   List<String> FindLinks2 = instance.Find2("finalText", "(" + ClientUtils.escapeQueryChars("'pobreza' 'poverty'") + ")", "endpoint", "cepalstat", 10);
        //  System.out.println(FindLinks2);
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
