/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    public synchronized JSONObject getLocation(String LocationEntity) throws SQLException, ParseException {
        JSONParser parser = new JSONParser();
        Cache instanceCache = Cache.getInstance();

        String CacheItem = instanceCache.get("GeonamesQuery=" + LocationEntity);

        if (CacheItem != null) {
            if (CacheItem.compareTo("null") == 0) {
                return null;
            }
            return (JSONObject) parser.parse(CacheItem);
        }

        String LocationEntityWithoutSpecialCharacters = LocationEntity.replaceAll("[-,_()]", " ");
        Map<String, String> mp = new HashMap<>();
        mp.put("q", LocationEntityWithoutSpecialCharacters);
        mp.put("country", "ec");
        mp.put("maxRows", "1");
        mp.put("username", "cedia");
        String Name = null;
        String Lon = null;
        String Lat = null;
        int sta=0;
        do {
            
            try {
                sta=0;
                String Http1 = HTTPUtils.Http("http://ws.geonames.org/searchJSON", mp);
                sta=1;
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
                    if (sta==1){
                        Thread.sleep(1000 * 3600 * 1);
                    }else{
                        Thread.sleep(100);
                    }
                    
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
            instanceCache.put("GeonamesQuery=" + LocationEntity, LocationResult.toJSONString());
        } else {
            instanceCache.put("GeonamesQuery=" + LocationEntity, "null");
        }

        return LocationResult;
    }

}
