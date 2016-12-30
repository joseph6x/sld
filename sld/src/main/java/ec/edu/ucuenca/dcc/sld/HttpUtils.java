/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;

/**
 *
 * @author cedia
 */
public class HttpUtils {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static synchronized String Http(String s) throws SQLException, IOException {

        String resp = "";
        final URL url = new URL(s);
        final URLConnection connection = url.openConnection();
        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:44.0) Gecko/20100101 Firefox/44.0");
        connection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        final Scanner reader = new Scanner(connection.getInputStream(), "UTF-8");
        while (reader.hasNextLine()) {
            final String line = reader.nextLine();
            resp += line + "\n";
        }
        reader.close();

        return resp;
    }

    public static String sendPost2(String operation, String body) throws Exception {
        //   System.out.println ("body "+body);
        String url = "http://api.cortical.io/rest/" + operation + "?retina_name=en_associative";

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("api-key", "0e30f600-7b62-11e6-a057-97f4c970893c");
        post.setHeader("content-type", "application/json");
        //   post.setHeader("api-key","0e30f600-7b62-11e6-a057-97f4c970893c");
        post.setHeader("charset", "utf-8");
        StringEntity params = new StringEntity(body);
        //List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        //urlParameters.add(new BasicNameValuePair("body", body));
        //urlParameters.add(new BasicNameValuePair("retina_name","en_associative"));
        /*urlParameters.add(new BasicNameValuePair("locale", ""));
		urlParameters.add(new BasicNameValuePair("caller", ""));
		urlParameters.add(new BasicNameValuePair("num", "12345"));*/

        post.setEntity(params);
        //  post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        // System.out.println("\nSending 'POST' request to URL : " + url);
        // System.out.println("Post parameters : " + post.getEntity());
        // System.out.println("Response Code : "
        //     + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        //  System.out.println(result.toString());
        int code = response.getStatusLine().getStatusCode();
        if (400 == code) {
            return "{\"overlappingAll\":0 , \"weightedScoring\": 0 } ";
        } else {
            return result.toString();
        }
    }

    public static synchronized String Http2(String s, Map<String, String> mp) throws SQLException, IOException {
        String resp = "";
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(s);
        method.getParams().setContentCharset("utf-8");

        client.getParams().setParameter("api-key", "58ef39e0-b91a-11e6-a057-97f4c970893c");
        client.getParams().setParameter("Content-Type", "application/json");

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

    public static String Escape(String palabras) {
        String txt = "";
        for (int i = 0; i < palabras.length(); i++) {
            char r = ' ';
            switch (palabras.charAt(i)) {
                case '+':
                case '-':
                case '&':
                case '|':
                case '!':
                case '(':
                case ')':
                case '{':
                case '}':
                case '[':
                case ']':
                case '^':
                case '~':
                case '*':
                case '?':
                case ':':
                case '\\':
                case '/':
                case '"':


                    break;
                default:
                    r = palabras.charAt(i);
                    break;
            }
            txt += r;
        }
        return txt;
    }

    public static String Escape2(String palabras) {
        String txt = "";
        for (int i = 0; i < palabras.length(); i++) {
            char r = ' ';
            switch (palabras.charAt(i)) {
                case '+':
                case '-':
                case '&':
                case '|':
                case '!':
                case '(':
                case ')':
                case '{':
                case '}':
                case '[':
                case ']':
                case '^':
                case '~':
                case '*':
                case '?':
                case ':':
                case '\\':
                case '/':
                    break;
                default:
                    r = palabras.charAt(i);
                    break;
            }
            txt += r;
        }
        return txt;
    }
    
    public static String traductorYandex(String palabras) {
        String url = "https://translate.yandex.net/api/v1.5/tr.json/translate";
        //String url = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20160321T160516Z.43cfb95e23a69315.6c0a2ae19f56388c134615f4740fbb1d400f15d3&lang=en&text=" + URLEncoder.encode(palabras, "UTF-8");
        Map<String, String> mp = new HashMap<>();
        mp.put("key", "trnsl.1.1.20160321T160516Z.43cfb95e23a69315.6c0a2ae19f56388c134615f4740fbb1d400f15d3");
        mp.put("lang", "en");
        mp.put("text", palabras);
        mp.put("options", "1");

        String rpalabras = "";

        String rs = "";
        try {
            String Http = Http2_(url, mp);
            rs = Http;
            String res = Http;
            JsonObject parse = JSON.parse(res).getAsObject();
            JsonArray asArray = parse.get("text").getAsArray();
            res = asArray.get(0).getAsString().value();
            rpalabras = res;
        } catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));
        }
        return rpalabras;
    }

    public static synchronized String Http2_(String s, Map<String, String> mp) throws SQLException, IOException {
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
