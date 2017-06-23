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
                            //List<String> FindLinks2 = instance.Find2("finalText", "(" + SearchTerms + ")", "endpoint", Repo, Integer.parseInt(limit));
                            List<String[]> FindLinks2 = instance.Find(new String[]{"endpoint", "finalText", "pathText"},
                                    new String[]{Repo, "(" + SearchTerms + ")", "(" + SearchTerms + ")"},
                                    new boolean[]{true, false, false},
                                    new String[]{"uri"}, false,
                                    Integer.parseInt(limit), true);
                            JSONArray Results2 = new JSONArray();
                            for (int i = 0; i < FindLinks2.size(); i++) {

                                JSONObject OneResult = new JSONObject();

                                String URI_ = FindLinks2.get(i)[0];

                                if ("repositorio".equals(Repo)) {
                                    LinksFilesUtiles.addProperty(OneResult, "Icon", Repo, URI_, true,
                                            false, ConfigInfo.getInstance().getConfig().get("DefaultImg").getAsString().value(), false,
                                            "http://www.w3.org/1999/xhtml/vocab#icon");

                                }
                                LinksFilesUtiles.addProperty(OneResult, "Title", Repo, URI_, true,
                                        false, URI_, false,
                                        "http://purl.org/dc/terms/title", "http://www.w3.org/2000/01/rdf-schema#label");

                                LinksFilesUtiles.addProperty(OneResult, "Language", Repo, URI_, true,
                                        false, URI_, true,
                                        "http://purl.org/dc/terms/title", "http://www.w3.org/2000/01/rdf-schema#label");

                                LinksFilesUtiles.addProperty(OneResult, "URI", URI_, URI_, false);

                                LinksFilesUtiles.addProperty(OneResult, "Handle", Repo, URI_, true,
                                        true, "http://interwp.cepal.org/sisgen/ConsultaIntegrada.asp?idIndicador=", false,
                                        "http://purl.org/ontology/bibo/handle");

                                LinksFilesUtiles.addProperty(OneResult, "Repository", URI_, Repo, false);
//
//                                LinksFilesUtiles.addProperty(OneResult, "Repository", Repo, URI_, true,
//                                        false, Repo, false,
//                                        "http://none/");

                                //Only for dspace repository!!!
                                if ("repositorio".equals(Repo)) {

                                    LinksFilesUtiles.addProperty(OneResult, "CallNumber", Repo, URI_, false,
                                            false, URI_, false,
                                            "http://myontology.org/callNumber");

                                    LinksFilesUtiles.addProperty(OneResult, "BibLevel", Repo, URI_, true,
                                            false, URI_, false,
                                            "http://myontology.org/bibLevel");

                                }
                                if ("cepalstat".equals(Repo)) {
                                    LinksFilesUtiles.addProperty(OneResult, "Date", Repo, URI_, true,
                                            false, "", false,
                                            "http://purl.org/dc/terms/date", "http://purl.org/dc/terms/issued", "http://purl.org/dc/terms/modified");
                                }
//                                String Img = LinksFilesUtiles.getIcon(FindLinks2.get(i));
//                                if (Img.compareTo(FindLinks2.get(i)) == 0) {
//                                    OneResult.put("Icon", ConfigInfo.getInstance().getConfig().get("DefaultImg").getAsString().value());
//                                } else {
//                                    OneResult.put("Icon", Img);
//                                }
//
//                                OneResult.put("Title", LinksFilesUtiles.getTitle(FindLinks2.get(i)));
//                                OneResult.put("Language", LinksFilesUtiles.getLang(FindLinks2.get(i)));
//                                OneResult.put("URI", FindLinks2.get(i));
//                                OneResult.put("Handle", LinksFilesUtiles.getHandle(FindLinks2.get(i)));
//                                OneResult.put("Repository", Repo);
//
//                                if ("repositorio".equals(Repo)) {
//
//                                    JSONArray OneArray = new JSONArray();
//                                    OneArray.addAll(LinksFilesUtiles.getCallNumber(FindLinks2.get(i)));
//                                    if (!OneArray.isEmpty()) {
//                                        OneResult.put("CallNumber", OneArray);
//                                    }
//                                    OneArray = new JSONArray();
//                                    OneArray.addAll(LinksFilesUtiles.getBibLevel(FindLinks2.get(i)));
//                                    if (!OneArray.isEmpty()) {
//                                        OneResult.put("BibLevel", OneArray);
//                                    }
//                                }
                                Results2.add(OneResult);

                            }

                            //sort
                            if ("cepalstat".equals(Repo)) {
                                LinksFilesUtiles.sortDate(Results2);
                            }
                            Results.addAll(Results2);

                        }

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
