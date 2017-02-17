/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author cedia
 */
public class Geonames {

    private Geonames() {
    }

    public static Geonames getInstance() {
        return GeonamesHolder.INSTANCE;
    }

    private static class GeonamesHolder {

        private static final Geonames INSTANCE = new Geonames();
    }

    public synchronized JSONObject getLocation(String LocationEntity) {

        String LocationEntityWithoutSpecialCharacters = LocationEntity.replaceAll("[-,_()]", " ");
        Map<String, String> mp = new HashMap<>();
        mp.put("q", LocationEntityWithoutSpecialCharacters);
        mp.put("country", "ec");
        mp.put("maxRows", "1");
        mp.put("username", "cedia");
        String Name = null;
        String Lon = null;
        String Lat = null;
        do {
            try {
                String Http1 = HTTPUtils.Http("http://ws.geonames.org/searchJSON", mp);
                JSONParser parser = new JSONParser();
                JSONObject MainObject = (JSONObject) parser.parse(Http1);
                JSONArray results = (JSONArray) MainObject.get("geonames");
                if (results.size() != 0) {
                    JSONObject oneResult = (JSONObject) results.get(0);
                    Name = (String) oneResult.get("name");
                    Lon = (String) oneResult.get("lng");
                    Lat = (String) oneResult.get("lat");
                    
                }
                break;
            } catch (Exception ex) {
                Logger.getLogger(Geonames.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    Thread.sleep(1000 * 3600 * 1);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(Geonames.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }

        } while (true);
        JSONObject LocationResult = null;
        if (Name != null && Lon != null && Lat != null) {
            LocationResult = new JSONObject();
            LocationResult.put("Name", Name);
            LocationResult.put("Longitude", Lon);
            LocationResult.put("Latitude", Lat);
            LocationResult.put("OriginalName", LocationEntity);
        }
        return LocationResult;
    }

}
