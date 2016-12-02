/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;

/**
 *
 * @author cedia
 */
public class YandexDictionary {
    
    
        public void Syn(String txt) throws SQLException, IOException{
            String Http = HttpUtils.Http("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=APIkey&lang=en-en&text="+URLEncoder.encode(txt, "UTF-8"));
            
            if (Http!=null){
                JsonObject parse = JSON.parse(Http);
                
                
            }
        
        }
    
    
}
