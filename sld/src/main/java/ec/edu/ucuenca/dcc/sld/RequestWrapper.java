/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author cedia
 */
public class RequestWrapper {

    private HttpServletRequest Request = null;

    public HttpServletRequest getRequest() {
        return Request;
    }

    public void setRequest(HttpServletRequest Request) {
        this.Request = Request;
    }

    public RequestWrapper(HttpServletRequest Request) {
        this.Request = Request;
    }

    private Map<String, String> getHeadersInfo() {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = Request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = Request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    private Map<String, String> getParametersInfo() {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = Request.getParameterNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = Request.getParameter(key);
            map.put(key, value);
        }
        return map;
    }
    
    private String getBodyInfo() throws IOException {
        String collect = Request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        return collect;
    }
    

    public String DataToText() throws IOException {
        Map<String, String> headersInfo = getHeadersInfo();
        Map<String, String> parametersInfo = getParametersInfo();
        String bodyInfo = getBodyInfo();
        
        return headersInfo.toString() + parametersInfo.toString()+bodyInfo;
    }

    public String RunRequest() throws IOException {

        String Result = null;
        Map<String, String> headersInfo = getHeadersInfo();
        Map<String, String> parametersInfo = getParametersInfo();
        String bodyInfo = getBodyInfo();
        String ForwardURL = parametersInfo.remove("ForwardCacheURL");
        
        do {

            try {
                Result = HTTPUtils.Http(ForwardURL, parametersInfo, headersInfo,bodyInfo);
                break;
            } catch (Exception ex) {
                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(RequestWrapper.class.getName()).log(Level.SEVERE, null, ex1);
                }
                Logger.getLogger(RequestWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (true);
        return Result;
    }

}
