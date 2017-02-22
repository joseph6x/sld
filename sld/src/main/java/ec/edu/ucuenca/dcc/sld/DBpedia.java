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

    private String[] DbpediaEndpoints = new String[]{"http://dbpedia.org/sparql", "http://es.dbpedia.org/sparql"};

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

        String QueryLon = "select ?lon { <" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lon . } limit 1";
        String QueryLat = "select ?lat { <" + URI + "> <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat . } limit 1";
        String lon = null;
        String lat = null;
        for (String Endpoint : DbpediaEndpoints) {
            do {
                if (lon != null && lat != null) {
                    break;
                }
                List<RDFNode> ResultLon = SPARQL.SimpleQuery(QueryLon, Endpoint, "lon");
                List<RDFNode> ResultLat = SPARQL.SimpleQuery(QueryLat, Endpoint, "lat");
                if (ResultLon != null && ResultLat != null) {
                    if (!ResultLon.isEmpty() && !ResultLat.isEmpty()) {
                        lon = ResultLon.get(0).asLiteral().getString();
                        lat = ResultLat.get(0).asLiteral().getString();
                    }
                    break;
                }
                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DBpedia.class.getName()).log(Level.SEVERE, null, ex);
                }

            } while (true);
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
