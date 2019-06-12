/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;

/**
 *
 * @author cedia
 */
public class ConfigInfo {

    private JsonObject config;
    private String spq;

    private ConfigInfo() {

        InputStream resourceAsStream = this.getClass().getResourceAsStream("/config.json");
        String theString;

        InputStream resourceAsStream2 = this.getClass().getResourceAsStream("/desc.sparql");

        try {
            theString = IOUtils.toString(resourceAsStream, Charset.defaultCharset().toString());
            config = JSON.parse(theString).getAsObject().get("Config").getAsObject();
            spq = IOUtils.toString(resourceAsStream2, Charset.defaultCharset().toString());;
        } catch (IOException ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static ConfigInfo getInstance() {
        return ConfigInfoHolder.INSTANCE;
    }

    private static class ConfigInfoHolder {

        private static final ConfigInfo INSTANCE = new ConfigInfo();
    }

    public JsonObject getConfig() {
        return config;
    }

    public void setConfig(JsonObject config) {
        this.config = config;
    }

    public String getSpq() {
        return spq;
    }

    public void setSpq(String spq) {
        this.spq = spq;
    }

}
