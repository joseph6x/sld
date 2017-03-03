/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
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
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {

            String ResultJSON = "";
            Cache instanceCache = Cache.getInstance();
            try {
                String Forward = request.getParameter("ForwardCacheURL");
                if (Forward != null && !Forward.isEmpty()) {
                    RequestWrapper Wrapper = new RequestWrapper(request);
                    String DataToText = Wrapper.DataToText();

                    String CacheItem = instanceCache.get("QueryForward=" + DataToText);

                    if (CacheItem != null) {
                        ResultJSON = CacheItem;
                    } else {
                        String RunRequestResult = Wrapper.RunRequest();
                        ResultJSON = RunRequestResult;

                        //System.out.println(RunRequestResult);
                        instanceCache.put("QueryForward=" + DataToText, RunRequestResult);
                    }
                } else {
                    String InputText = request.getParameter("InputText");
                    String InputURI = request.getParameter("URI");
                    //SearchType:
                    //0:No valid input submitted, error
                    //1:Text, use NER and Geonames
                    //2:URI, use DBpedia
                    int SearchType = (InputText != null && !InputText.isEmpty()) ? 1 : (InputURI != null && !InputURI.isEmpty()) ? 2 : 0;
                    if (SearchType != 0) {
                        String InputValue = SearchType == 1 ? InputText : InputURI;
                        String cacheItem = instanceCache.get("QueryText=" + InputValue);
                        if (cacheItem != null) {
                            ResultJSON = cacheItem;
                        } else {
                            JSONArray Results = new JSONArray();
                            if (SearchType == 1) {
                                Geonames instanceGeonames = Geonames.getInstance();
                                SNER instanceSNER = SNER.getInstance();
                                List<String> NamedEntities = instanceSNER.GetNamedEntities(InputValue);
                                for (String aLocation : NamedEntities) {
                                    JSONObject location = instanceGeonames.getLocation(aLocation);
                                    if (location != null) {
                                        Results.add(location);
                                    }
                                }
                            } else {
                                DBpedia instanceDBpedia = DBpedia.getInstance();
                                JSONObject location = instanceDBpedia.getLocation(InputValue);
                                if (location != null) {
                                    Results.add(location);
                                }
                            }
                            ResultJSON = Results.toJSONString();
                            instanceCache.put("QueryText=" + InputValue, ResultJSON);
                        }
                    } else {
                        throw new Exception("No valid params found... Required: InputText or InputURI");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(new PrintStream(System.out));
                ResultJSON = "{\"error\":\"" + e + "\"}";
            }

            out.println(ResultJSON);
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
