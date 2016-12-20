/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        try (PrintWriter out = response.getWriter()) {
            String res = "";
            try {

                String parameter_ = request.getParameter("repositories");
                String parameter__ = request.getParameter("type");
                String parameter___ = request.getParameter("query");

                if (parameter_ != null && !parameter_.isEmpty() && parameter__ != null && !parameter__.isEmpty() && parameter___ != null && !parameter___.isEmpty()) {

                    SolrConnection instance = SolrConnection.getInstance();

                    String[] rep = parameter_.split(";");
                    String Repo = rep[0];
                    String limit = rep[1];

                    String txt = "";

                    switch (parameter__) {
                        case "handle":
                            String[] dt = parameter___.split("_|/|#|=");
                            parameter___ = dt[dt.length - 1];
                        case "id":
                            String[] FindOne = instance.FindOne2("uri", "*/" + parameter___, "originalText", "uri", "finalText", "endpoint");
                            String[] FindOne2 = instance.FindOne2("uri", "*_" + parameter___, "originalText", "uri", "finalText", "endpoint");
                            String[] FindOne3 = instance.FindOne2("uri", "*#" + parameter___, "originalText", "uri", "finalText", "endpoint");
                            String[] FindOne4 = instance.FindOne2("uri", "*=" + parameter___, "originalText", "uri", "finalText", "endpoint");

                            List<String[]> ls = new ArrayList<String[]>();
                            ls.add(FindOne);
                            ls.add(FindOne2);
                            ls.add(FindOne3);
                            ls.add(FindOne4);
                            ls.removeAll(Collections.singleton(null));

                            if (!ls.isEmpty()) {
                                txt = ls.get(0)[2];
                            } else {
                                throw new Exception("No handle/id found..." + parameter___);
                            }

                            txt = "";
                            break;
                        case "uri":
                            String[] FindOne_ = instance.FindOne2("uri", parameter___, "originalText", "uri", "finalText", "endpoint");
                            if (FindOne_ != null) {
                                txt = FindOne_[2];
                            } else {
                                throw new Exception("No URI found..." + parameter___);
                            }
                            break;
                        case "keywords":
                            txt = parameter___;
                            break;
                    }

                }

                String parameter = request.getParameter("uri");
                String parameter1 = request.getParameter("handle");
                String parameter2 = request.getParameter("id");
                String parameter3 = request.getParameter("keywords");

                List<String> FindLinks = new ArrayList<>();

                if (parameter != null) {
                    URL url = new URL(parameter);
                    FindLinks = LinksFilesUtiles.getLinks(0, parameter);
                } else if (parameter1 != null) {
                    String[] dt = parameter1.split("_|/|#|=");
                    parameter1 = dt[dt.length - 1];
                    FindLinks = LinksFilesUtiles.getLinks(1, parameter1);
                } else if (parameter2 != null) {
                    FindLinks = LinksFilesUtiles.getLinks(1, parameter2);
                } else if (parameter3 != null) {
                    SolrConnection instance = SolrConnection.getInstance();
                    List<String> FindLinks2 = new ArrayList<>();
                    FindLinks = instance.Find("finalText", "(" + parameter3 + ")", ConfigInfo.getInstance().getConfig().get("LinksThreshold").getAsNumber().value().doubleValue());
                    for (int i = 0; i < FindLinks.size(); i++) {
                        FindLinks2.add(FindLinks.get(i).split("\\|")[1]);
                    }
                    FindLinks = FindLinks2;
                }

                res = "[";
                String txt = "";
                for (int i = 0; i < FindLinks.size(); i++) {

                    txt += "{\"URI\":\"" + FindLinks.get(i) + "\", \"Handle\":\"" + LinksFilesUtiles.getHandle(FindLinks.get(i)) + "\"}";

                    txt += (i == FindLinks.size() - 1 ? "" : ",");
                }
                res += txt + "]";
            } catch (Exception e) {
                e.printStackTrace(new PrintStream(System.out));
                res = "{\"error\":\"" + e + "\"}";
            }

            out.println(res);
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
