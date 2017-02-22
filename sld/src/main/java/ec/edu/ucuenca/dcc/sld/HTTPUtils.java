/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 *
 * @author cedia
 */
public class HTTPUtils {

    public static synchronized String Http(String s, Map<String, String> mp,  Map<String, String> mh, String body) throws  IOException {
        String resp = "";
        HttpClient client = new HttpClient();
        
        PostMethod method = new PostMethod(s);
        method.getParams().setContentCharset("utf-8");
        //Add any parameter if u want to send it with Post req.
        for (Entry<String, String> mcc : mp.entrySet()) {
            method.addParameter(mcc.getKey(), mcc.getValue());
        }
        for (Entry<String, String> mcc : mh.entrySet()) {
            method.addRequestHeader(mcc.getKey(), mcc.getValue());
        }
        method.setRequestBody(body);
        
        
        int statusCode = client.executeMethod(method);
        if (statusCode != -1) {
            InputStream in = method.getResponseBodyAsStream();
            final Scanner reader = new Scanner(in, "UTF-8");
            while (reader.hasNextLine()) {
                final String line = reader.nextLine();
                resp += line + "\n";
            }
            reader.close();
        }
        return resp;
    }
    
    
    public static synchronized String Http(String s, Map<String, String> mp) throws  IOException {
        String resp = "";
        HttpClient client = new HttpClient();
        
        PostMethod method = new PostMethod(s);
        method.getParams().setContentCharset("utf-8");
        //Add any parameter if u want to send it with Post req.
        for (Entry<String, String> mcc : mp.entrySet()) {
            method.addParameter(mcc.getKey(), mcc.getValue());
        }
        
        int statusCode = client.executeMethod(method);
        if (statusCode != -1) {
            InputStream in = method.getResponseBodyAsStream();
            final Scanner reader = new Scanner(in, "UTF-8");
            while (reader.hasNextLine()) {
                final String line = reader.nextLine();
                resp += line + "\n";
            }
            reader.close();
        }
        return resp;
    }

}
