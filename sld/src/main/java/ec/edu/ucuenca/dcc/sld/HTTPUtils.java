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
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 *
 * @author cedia
 */
public class HTTPUtils {

    public static synchronized String Http(String s, Map<String, String> mp, Map<String, String> mh, String body) throws IOException {
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

        StringRequestEntity entity = new StringRequestEntity(body, "application/json", "UTF-8");
        method.setRequestEntity(entity);

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
        System.out.println("http" + resp);
        return resp;
    }

    public static synchronized String Http(String s, Map<String, String> mp) throws IOException {
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

    public static String sendPost(String s, Map<String, String> mp, Map<String, String> mh, String body) throws Exception {

        //System.out.println(s);
        Properties props = System.getProperties();
        props.setProperty("java.net.preferIPv4Stack", "true");
        String url = s;
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

        if (!mp.isEmpty()) {
            for (Entry<String, String> mcc : mp.entrySet()) {
                postParameters.add(new BasicNameValuePair(mcc.getKey(), mcc.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(postParameters, HTTP.UTF_8));
        }

        for (Entry<String, String> mcc : mh.entrySet()) {
            post.setHeader(mcc.getKey(), mcc.getValue());
        }
        //post.setHeader("charset", "utf-8");
        //post.setHeader("Accept-Encoding", "gzip,deflate");

        if (body != null && !body.trim().isEmpty()) {
            StringEntity params = new StringEntity(body.replaceAll("\\P{IsLatin}", " "), HTTP.UTF_8);
            post.setEntity(params);
        }

        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        int code = response.getStatusLine().getStatusCode();
        //System.out.println(code);
        if (200 != code) {
            System.out.println(s);
            System.out.println(mp);
            System.out.println(mh);
            System.out.println(code);
            System.out.println(result);

            return null;
        } else {
            return result.toString();
        }
    }

/*    public static String sendPostNew(String s, Map<String, String> mp, Map<String, String> mh, String body) throws Exception {

        HttpPost httpPost = new HttpPost(s);

        //System.out.println(s);
        Properties props = System.getProperties();
        props.setProperty("java.net.preferIPv4Stack", "true");
        String url = s;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

        if (!mp.isEmpty()) {
            for (Entry<String, String> mcc : mp.entrySet()) {
                postParameters.add(new BasicNameValuePair(mcc.getKey(), mcc.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(postParameters, HTTP.UTF_8));
        }

        for (Entry<String, String> mcc : mh.entrySet()) {
            post.setHeader(mcc.getKey(), mcc.getValue());
        }
        //post.setHeader("charset", "utf-8");
        //post.setHeader("Accept-Encoding", "gzip,deflate");

        if (body != null && !body.trim().isEmpty()) {
            StringEntity params = new StringEntity(body, HTTP.UTF_8);
            post.setEntity(params);
        }

        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        int code = response.getStatusLine().getStatusCode();
        //System.out.println(code);
        if (200 != code) {
            System.out.println(s);
            System.out.println(mp);
            System.out.println(mh);
            System.out.println(code);
            System.out.println(result);

            return null;
        } else {
            return result.toString();
        }
    }*/

    public static String sendPost2(String operation, String body) throws Exception {
        //   System.out.println ("body "+body);
        String url = "http://api.cortical.io/rest/" + operation + "?retina_name=en_associative";

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        String USER_AGENT = "Mozilla/5.0";
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
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println(result.toString());
        int code = response.getStatusLine().getStatusCode();
        if (400 == code) {
            return "{\"overlappingAll\":0 , \"weightedScoring\": 0 } ";
        } else {
            return result.toString();
        }
    }

}
