/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        try (PrintWriter out = response.getWriter()) {
            String res = "";
            try {

                String Callback = request.getParameter("callback");
                if (Callback != null && !Callback.isEmpty()) {
                    JSONP = true;
                    Callback_ = Callback;
                }

                String RepositoriesList = request.getParameter("repositories");
                String QueryType = request.getParameter("type");
                String QueryText = request.getParameter("query");

                if (RepositoriesList != null && !RepositoriesList.isEmpty() && QueryType != null && !QueryType.isEmpty() && QueryText != null && !QueryText.isEmpty()) {

                    Cache instanceCache = Cache.getInstance();
                    String KeyCache = QueryType + "+" + RepositoriesList + "+" + QueryText;
                    String mD5Key = instanceCache.getMD5(KeyCache);

                    String CacheResult = instanceCache.get("L1=" + mD5Key);

                    if (CacheResult != null) {
                        res = CacheResult;
                    } else {
                        SolrConnection instance = SolrConnection.getInstance();

                        String SearchTerms = "";

                        switch (QueryType) {
                            case "handle":
                                String[] dt = QueryText.split("_|/|#|=");
                                QueryText = dt[dt.length - 1];
                            case "id":
                                String[] FindOne = instance.FindOne2("uri", "*/" + QueryText, "originalText", "uri", "finalText", "endpoint");
                                String[] FindOne2 = instance.FindOne2("uri", "*_" + QueryText, "originalText", "uri", "finalText", "endpoint");
                                String[] FindOne3 = instance.FindOne2("uri", "*#" + QueryText, "originalText", "uri", "finalText", "endpoint");
                                String[] FindOne4 = instance.FindOne2("uri", "*=" + QueryText, "originalText", "uri", "finalText", "endpoint");

                                List<String[]> ls = new ArrayList<String[]>();
                                ls.add(FindOne);
                                ls.add(FindOne2);
                                ls.add(FindOne3);
                                ls.add(FindOne4);
                                ls.removeAll(Collections.singleton(null));

                                if (!ls.isEmpty()) {
                                    SearchTerms = ls.get(0)[2];
                                    SearchTerms = HttpUtils.Escape(SearchTerms);
                                } else {
                                    throw new Exception("No handle/id found..." + QueryText);
                                }
                                break;
                            case "uri":
                                String[] FindOne_ = instance.FindOne("uri", QueryText, "originalText", "uri", "finalText", "endpoint");
                                if (FindOne_ != null) {
                                    SearchTerms = FindOne_[2];
                                    SearchTerms = HttpUtils.Escape(SearchTerms);
                                } else {
                                    throw new Exception("No URI found..." + QueryText);
                                }
                                break;
                            case "keywords":
                                SearchTerms = QueryText;
                                SearchTerms = HttpUtils.Escape2(SearchTerms);
                                break;
                        }
                        String traductorYandex = HttpUtils.traductorYandex(SearchTerms);

                        if (!traductorYandex.trim().isEmpty()) {
                            SearchTerms = SearchTerms + "," + traductorYandex;
                        }

                        SearchTerms = SearchTerms.replace(",", " ").trim();
                        String[] rep = RepositoriesList.split(";");
                        //List<String> FindLinks = new ArrayList<>();

                        //res = "[";
                        JSONArray Results = new JSONArray();

                        List<String> t_ = new ArrayList<>();

                        for (int j = 0; j < rep.length; j++) {
                            String end = rep[j];
                            String Repo = end.split(":")[0];
                            String limit = end.split(":")[1];
                            List<String> FindLinks2 = instance.Find2("finalText", "(" + SearchTerms + ")", "endpoint", Repo, Integer.parseInt(limit));
                            for (int i = 0; i < FindLinks2.size(); i++) {
                                //String txt2_ = "{\"Icon\":\"" + LinksFilesUtiles.getIcon(FindLinks2.get(i))
                                //          + "\", \"Title\":\"" + LinksFilesUtiles.getTitle(FindLinks2.get(i)).replaceAll("\"", "'") + "\", \"URI\":\""
                                //        + FindLinks2.get(i) + "\", \"Handle\":\"" + LinksFilesUtiles.getHandle(FindLinks2.get(i)) + "\", \"Repository\":\""
                                //        + Repo + "\"}";
                                // t_.add(txt2_);

                                JSONObject OneResult = new JSONObject();

                                String Img = LinksFilesUtiles.getIcon(FindLinks2.get(i));
                                if (Img.compareTo(FindLinks2.get(i)) == 0) {
                                    OneResult.put("Icon", ConfigInfo.getInstance().getConfig().get("DefaultImg").getAsString().value());
                                } else {
                                    OneResult.put("Icon", Img);
                                }

                                OneResult.put("Title", LinksFilesUtiles.getTitle(FindLinks2.get(i)));
                                OneResult.put("Language", LinksFilesUtiles.getLang(FindLinks2.get(i)));
                                OneResult.put("URI", FindLinks2.get(i));
                                OneResult.put("Handle", LinksFilesUtiles.getHandle(FindLinks2.get(i)));
                                OneResult.put("Repository", Repo);

                                if (Repo == "repositorio") {

                                    JSONArray OneArray = new JSONArray();
                                    OneArray.addAll(LinksFilesUtiles.getCallNumber(FindLinks2.get(i)));
                                    if (!OneArray.isEmpty()) {
                                        OneResult.put("CallNumber", OneArray);
                                    }
                                    OneArray = new JSONArray();
                                    OneArray.addAll(LinksFilesUtiles.getBibLevel(FindLinks2.get(i)));
                                    if (!OneArray.isEmpty()) {
                                        OneResult.put("BibLevel", OneArray);
                                    }
                                }

                                Results.add(OneResult);

                                //txt2 += (i == FindLinks2.size() - 1 && j == rep.length - 1 ? "" : ",");
                            }
                        }
                        //String txt2 = "";
                        // for (int j = 0; j < t_.size(); j++) {
                        //    txt2 += t_.get(j);
                        //    txt2 += (j == t_.size() - 1 ? "" : ",");
                        // }

                        //res += txt2 + "]";
                        res = Results.toJSONString();
                        instanceCache.put(mD5Key, "L1=" + res);
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
