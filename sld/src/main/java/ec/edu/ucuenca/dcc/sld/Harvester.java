/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import com.hp.hpl.jena.rdf.model.RDFNode;
import ec.edu.ucuenca.dcc.sld.utils.BoundedExecutor;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author cedia
 */
public class Harvester {

  private String Endpoint;

  private String MainClass;

  private String Query;
  private String Name;

  private String Filter;

  private String TwoSteps;
  private String Date;

  public Harvester(String Name, String Endpoint, String MainClass, String Query, String Filter, String Date, String TwoSteps) {
    this.Endpoint = Endpoint;
    this.MainClass = MainClass;
    this.Query = Query;
    this.Name = Name;
    this.Filter = Filter;
    this.Date = Date;
    this.TwoSteps = TwoSteps;
  }

  public void GarbageCollector() throws IOException, Exception {
    SPARQL sp = new SPARQL();
    SolrConnection instance = SolrConnection.getInstance();
    List<String[]> Find = instance.Find(new String[]{"endpoint"}, new String[]{Name}, new boolean[]{true}, new String[]{"uri"}, true, -1, true);
    for (String[] oneF : Find) {
      String uri = oneF[0];
      String qr = "ask { <" + uri + "> a <" + MainClass + "> . }";
      boolean SimpleQueryAsk = sp.SimpleQueryAsk(qr, Endpoint);
      if (!SimpleQueryAsk) {
        //Remove old index entry.
        instance.remove(new String[]{"uri"}, new String[]{uri}, new boolean[]{true}, true);
      }
    }

  }

  public void Harvest() throws SolrServerException, IOException, Exception {

    BoundedExecutor threadPool = BoundedExecutor.getThreadPool(2);

    int bulk = 1000;
    final SPARQL sp = new SPARQL();
    String Count = "select (count(*) as ?c) where { ?r a <" + MainClass + "> . @@@ }";
    String resources = "select ?r where { ?r a <" + MainClass + "> . @@@  } limit " + bulk;

    //
    if (Filter != null) {
      Count = Count.replaceAll("@@@", "filter contains(str(?r),'" + Filter + "') . ");
      resources = resources.replaceAll("@@@", "filter contains(str(?r),'" + Filter + "') . ");
    } else {
      Count = Count.replaceAll("@@@", "");
      resources = resources.replaceAll("@@@", "");
    }

    List<RDFNode> SimpleQuery = sp.SimpleQuery(Count, Endpoint, "c");
    RDFNode get = SimpleQuery.get(0);
    int aInt = get.asLiteral().getInt();
    for (int i = 0; i < aInt; i += 1000) {
      final int ix = i;
      final int aIntx = aInt;
      String qry = resources + " offset " + i;
      final List<RDFNode> SimpleQuery1 = sp.SimpleQuery(qry, Endpoint, "r");

      threadPool.submitTask(new Runnable() {
        @Override
        public void run() {
          try {
            for (RDFNode d : SimpleQuery1) {
              String uri = d.asResource().getURI();
              //System.out.println("Checking ...  " + i + " / " + aInt + " / " + uri);
              if (!SolrConnection.getInstance().exists(uri)) {
                System.out.println("Harvesting ...  " + ix + " / " + aIntx + " / " + uri);
                List<String> SimpleQuery2_ = new ArrayList<>();
                if (TwoSteps != null) {
                  String QueryTwo = TwoSteps.replaceAll("\\|\\?\\|", uri);
                  SimpleQuery2_ = sp.SimpleQueryString(QueryTwo, Endpoint, "l");
                }

                String Query2 = Query.replaceAll("\\|\\?\\|", uri);
                List<RDFNode> SimpleQuery2 = sp.SimpleQuery(Query2, Endpoint, "d");

                AddUpdate(uri, SimpleQuery2, SimpleQuery2_);

              }
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      });

    }
    threadPool.end();

  }

  public void AddUpdate(String uri, List<RDFNode> ls, List<String> lsTwo) throws SolrServerException, IOException, Exception {

    //System.out.println(ls);
    String txt = "";

    for (RDFNode a : ls) {
      try {
        if (a.isLiteral()) {
          txt += a.asLiteral().getString() + ", ";
        } else {
          List<String> DbpediaLabel = new ArrayList<>();//DbpediaLabel(a.asResource().getURI());
          DbpediaLabel.add(HttpUtils.Escape2(a.asResource().getLocalName()).trim());

          for (int j = 0; j < DbpediaLabel.size(); j++) {
            txt += DbpediaLabel.get(j) + ", ";
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    String txtTwo = "";
    for (String a : lsTwo) {
      txtTwo += a + ", ";
    }

    JSONObject OneResult = new JSONObject();
    String Repo = Name;
    String URI_ = uri;
//        if ("repositorio".equals(Repo)) {
//            LinksFilesUtiles.addProperty(OneResult, "Icon", Repo, URI_, true,
//                    false, ConfigInfo.getInstance().getConfig().get("DefaultImg").getAsString().value(), false,
//                    "http://www.w3.org/1999/xhtml/vocab#icon");
//        }
    LinksFilesUtiles.addProperty(OneResult, "Title", Repo, URI_, true,
            false, URI_, false,
            "http://purl.org/dc/terms/title", "http://www.w3.org/2000/01/rdf-schema#label");

//        LinksFilesUtiles.addProperty(OneResult, "Language", Repo, URI_, true,
//                false, URI_, true,
//                "http://purl.org/dc/terms/title", "http://www.w3.org/2000/01/rdf-schema#label");
    LinksFilesUtiles.addProperty(OneResult, "Handle", Repo, URI_, true,
            true, "http://interwp.cepal.org/sisgen/ConsultaIntegrada.asp?idIndicador=", false,
            "http://purl.org/ontology/bibo/handle");

//        if ("repositorio".equals(Repo)) {
//            LinksFilesUtiles.addProperty(OneResult, "CallNumber", Repo, URI_, false,
//                    false, URI_, false,
//                    "http://myontology.org/callNumber");
//            LinksFilesUtiles.addProperty(OneResult, "BibLevel", Repo, URI_, true,
//                    false, URI_, false,
//                    "http://myontology.org/bibLevel");
//        }
    List<Map.Entry<String, String>> lsEntries = new ArrayList<>();
    lsEntries.add(new AbstractMap.SimpleEntry("uri", uri));
    lsEntries.add(new AbstractMap.SimpleEntry("originalText", txt));
    lsEntries.add(new AbstractMap.SimpleEntry("originalTextSyn", ""));
    lsEntries.add(new AbstractMap.SimpleEntry("finalText", txt));
    lsEntries.add(new AbstractMap.SimpleEntry("state", "0"));
    lsEntries.add(new AbstractMap.SimpleEntry("endpoint", this.Name));
    lsEntries.add(new AbstractMap.SimpleEntry("pathText", txtTwo));
    addItems(OneResult, lsEntries);
    SolrConnection.getInstance().insert(lsEntries);
    //state
    //SolrConnection.getInstance().insert(new String[]{"uri", "originalText", "originalTextSyn", "finalText", "state", "endpoint", "pathText"},
    //      new Object[]{uri, txt, "", txt, 0, this.Name, txtTwo});
  }

  public void addItems(JSONObject data, List<Map.Entry<String, String>> lsEntries) {
    Set<Map.Entry> entrySet = data.entrySet();
    for (Map.Entry amp : entrySet) {
      String k = amp.getKey().toString();
      if (amp.getValue() instanceof JSONArray) {
        JSONArray v = (JSONArray) amp.getValue();
        for (Object o : v) {
          String va = o.toString();
          lsEntries.add(new AbstractMap.SimpleEntry<>(k, va));
        }
      } else {
        String v = amp.getValue().toString();
        lsEntries.add(new AbstractMap.SimpleEntry<>(k, v));
      }
    }
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

  public String getFilter() {
    return Filter;
  }

  public void setFilter(String Filter) {
    this.Filter = Filter;
  }

  public String getTwoSteps() {
    return TwoSteps;
  }

  public void setTwoSteps(String TwoSteps) {
    this.TwoSteps = TwoSteps;
  }

  public String getDate() {
    return Date;
  }

  public void setDate(String Date) {
    this.Date = Date;
  }

}
