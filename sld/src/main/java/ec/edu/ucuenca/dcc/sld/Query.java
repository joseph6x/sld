/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.methods.HttpPost;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author cedia
 */
public class Query extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        boolean JSONP = false;
        String Callback_ = "";
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String res = "";
            try {

                String Callback = request.getParameter("callback");
                if (Callback != null && !Callback.isEmpty()) {
                    JSONP = true;
                    Callback_ = Callback;
                }
                String uri = request.getParameter("uri");
                String limit = request.getParameter("limit");
                if (uri != null && !uri.isEmpty() && limit != null && !limit.isEmpty()) {

                    //Cache instanceCache = Cache.getInstance();
                    //SearchTerms = SemanticFilter.filter2(SearchTerms);
                    SolrConnection instance = SolrConnection.getInstance();
                    String[] FindOne2 = instance.FindOne("uri", uri, "endpoint", "endpoint", "finalText", "finalText");
                    if (FindOne2 != null) {
                        String tipo = FindOne2[0];
                        String text = FindOne2[2];
                        String filter2 = SemanticFilter.filter2(text);
                        JSONArray FindModX = instance.FindModX(tipo, filter2, Integer.parseInt(limit), "0", uri);
                        List<String> lrq = new ArrayList<>();
                        for (Iterator it = FindModX.iterator(); it.hasNext();) {
                            JSONObject a = (JSONObject) it.next();
                            String toString = a.get("URI").toString();
                            if (toString.compareTo(uri) != 0) {
                                lrq.add("<" + toString + ">");
                            }
                        }
                        String[] split = new String[lrq.size()];
                        split = lrq.toArray(split);
                        String join = String.join(" ", split);
                        ConfigInfo instance1 = ConfigInfo.getInstance();
                        String replaceAll = instance1.getSpq().replaceAll("\\|\\?\\|", join);
                        String runQuery = runQuery(replaceAll);
                        res = runQuery;
                    } else {
                        throw new Exception("URI not found");
                    }

                } else {
                    throw new Exception("No valid params found... repositories, type, query");
                }

            } catch (Exception e) {
                e.printStackTrace(new PrintStream(System.out));
                res = "{\"error\":\"" + e + "\"}";
            }
            if (JSONP) {
                response.setContentType("application/javascript;charset=UTF-8");
                out.println(Callback_ + "(" + res + ");");
            } else {
                response.setContentType("application/json;charset=UTF-8");
                out.println(res);
            }

            out.flush();
            out.close();

        }
    }

    public String runQuery(String qe) throws IOException {
        Map<String, String> q = new HashMap<>();
        q.put("query", qe);
        return Http2("http://localhost:3030/library/sparql", q);
    }

    public String Http2(String s, Map<String, String> mp) throws IOException {
        String resp = "";
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(s);
        method.getParams().setContentCharset("utf-8");
        method.addRequestHeader("Accept", "application/sparql-results+json,*/*;q=0.9");
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

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
