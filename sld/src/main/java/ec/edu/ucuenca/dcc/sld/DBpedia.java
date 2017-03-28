/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import com.hp.hpl.jena.rdf.model.RDFNode;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author cedia
 */
public class DBpedia {

    private String[] DbpediaEndpoints = new String[]{"http://data.utpl.edu.ec/serendipity/oar/sparql", "http://190.15.141.102:8891/myservice/sparql", "http://190.15.141.66:8891/myservice/sparql"};

    private DBpedia() {
    }

    public static DBpedia getInstance() {
        return DBpediaHolder.INSTANCE;
    }

    private static class DBpediaHolder {

        private static final DBpedia INSTANCE = new DBpedia();
    }

    public synchronized JSONObject getLocation(String URI) throws SQLException, ParseException {
        JSONParser parser = new JSONParser();
        Cache instanceCache = Cache.getInstance();
        String CacheItem = instanceCache.get("DBpediaQuery=" + URI);
        if (CacheItem != null) {
            if (CacheItem.compareTo("null") == 0) {
                return null;
            }
            return (JSONObject) parser.parse(CacheItem);
        }

        //String QueryLon = "select ?lon { <" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lon . } limit 1";
        //String QueryLat = "select ?lat { <" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat . } limit 1";
        String CompleteQuery = "select ?lon ?lat {\n"
                + "	{service silent <http://dbpedia.org/sparql>{\n"
                + "		select ?lon ?lat {\n"
                + "			<" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lon .\n"
                + "			<" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat .\n"
                + "		} limit 1\n"
                + "	}}union{service silent <http://dbpedia-live.openlinksw.com/sparql>{\n"
                + "		select ?lon ?lat {\n"
                + "			<" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lon .\n"
                + "			<" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat .\n"
                + "		} limit 1\n"
                + "	}}union{service silent <http://de.dbpedia.org/sparql>{\n"
                + "		select ?lon ?lat {\n"
                + "			<" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lon .\n"
                + "			<" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat .\n"
                + "		} limit 1\n"
                + "	}} "+"union{service silent <http://es.dbpedia.org/sparql>{\n"
                + "		select ?lon ?lat {\n"
                + "			<" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lon .\n"
                + "			<" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat .\n"
                + "		} limit 1\n"
                + "	}}\n"
                + "} limit 1";

        String lon = null;
        String lat = null;
        for (String Endpoint : DbpediaEndpoints) {
            boolean data = false;

            if (instanceCache.BlackList.contains(Endpoint)) {
                continue;
            }
            int sta = 0;
            do {
                if (lon != null && lat != null) {
                    data = true;
                    break;
                }
                if (sta == 100) {
                    System.out.println("Timeout: " + Endpoint + " --> " + URI);
                    instanceCache.BlackList.add(Endpoint);
                    lon = null;
                    lat = null;
                    break;
                }
                sta++;
                //List<RDFNode> ResultLon = SPARQL.SimpleQuery(QueryLon, Endpoint, "lon");
                //List<RDFNode> ResultLat = SPARQL.SimpleQuery(QueryLat, Endpoint, "lat");
                List<List<RDFNode>> ResultLonLat = SPARQL.SimpleDoubleQuery(CompleteQuery, Endpoint, "lon", "lat");
                if (ResultLonLat != null && ResultLonLat.size() == 2 && ResultLonLat.get(0) != null && ResultLonLat.get(1) != null) {
                    if (!ResultLonLat.get(0).isEmpty() && !ResultLonLat.get(1).isEmpty()) {
                        lon = ResultLonLat.get(0).get(0).asLiteral().getString();
                        lat = ResultLonLat.get(1).get(0).asLiteral().getString();
                    }
                    data = true;
                    break;

                }
                try {
                    Thread.sleep(100 * sta);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DBpedia.class.getName()).log(Level.SEVERE, null, ex);
                }

            } while (true);
            if (data) {
                break;
            }
        }
        JSONObject LocationResult = null;
        if (lon != null && lat != null) {
            LocationResult = new JSONObject();
            LocationResult.put("URI", URI);
            LocationResult.put("Longitude", lon);
            LocationResult.put("Latitude", lat);
            instanceCache.put("DBpediaQuery=" + URI, LocationResult.toJSONString());
        } else {
            instanceCache.put("DBpediaQuery=" + URI, "null");
        }

        return LocationResult;
    }

}
