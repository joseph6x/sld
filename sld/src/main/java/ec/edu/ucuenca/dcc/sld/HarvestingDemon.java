/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.apache.solr.client.solrj.SolrServerException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author cedia
 */
public class HarvestingDemon extends Thread {

    @Override
    public void run() {
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();
        for (int i = 0; i < get.size(); i++) {
            try {
                JsonObject get1 = get.get(i).getAsObject();
                Harvester hv = new Harvester(get1.get("Endpoint").getAsString().value(), get1.get("MainClass").getAsString().value(), get1.get("Query").getAsString().value());
                hv.Harvest();
            } catch (SolrServerException ex) {
                Logger.getLogger(HarvestingDemon.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HarvestingDemon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void doShutdown() {

    }

}
