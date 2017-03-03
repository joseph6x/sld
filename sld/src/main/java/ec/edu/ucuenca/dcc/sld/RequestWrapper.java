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
    private String Body = null;
    private long WaitTime = 0;

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
        map.remove("host");
        map.remove("content-length");
        map.remove("content-type");
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
        String collect = Body;
        if (Body == null) {
            collect = Request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Body = collect;
        }
        return collect;
    }

    public String DataToText() throws IOException {
        Map<String, String> headersInfo = getHeadersInfo();
        Map<String, String> parametersInfo = getParametersInfo();
        String bodyInfo = getBodyInfo();

        return headersInfo.toString() + parametersInfo.toString() + bodyInfo;
    }

    public String RunRequest() throws IOException {

        String Result = null;
        Map<String, String> headersInfo = getHeadersInfo();
        Map<String, String> parametersInfo = getParametersInfo();
        String bodyInfo = getBodyInfo();
        
        
        //Strange bug in cortical.io
        if (bodyInfo.contains("nick meijer")){
            bodyInfo= bodyInfo.replace("nick meijer", "");
        }
        ///
        
        
        String ForwardURL = parametersInfo.remove("ForwardCacheURL");
        //headersInfo.remove("user-agent");

        do {

            try {
                Result = HTTPUtils.sendPost(ForwardURL, parametersInfo, headersInfo, bodyInfo);
                if (Result != null) {
                    break;
                }
            } catch (Exception ex) {
                if (WaitTime > 10000) {
                    Logger.getLogger(RequestWrapper.class.getName()).log(Level.SEVERE, null, ex);
                    WaitTime -= 100;
                }
            }

            if (WaitTime > 10000) {
                WaitTime -= 100;
                System.out.println("FW:" + ForwardURL);
                System.out.println("PA:" + parametersInfo);
                System.out.println("HE:" + headersInfo);
                System.out.println("BO:" + bodyInfo);
            }
            try {
                Thread.sleep(WaitTime);
                WaitTime += 100;
            } catch (Exception ex1) {
                //Logger.getLogger(RequestWrapper.class.getName()).log(Level.SEVERE, null, ex1);
            }

        } while (true);
        return Result;
    }

}
