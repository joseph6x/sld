/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;

/**
 *
 * @author cedia
 */
public class HarvestingDemon extends Thread {

    @Override
    public void run() {
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();

        for (;;) {
            try {
                Thread.sleep(100000);
                for (int i = 0; i < get.size(); i++) {
                    try {
                        JsonObject get1 = get.get(i).getAsObject();
                        boolean ApplyFilter = get1.get("ApplyFilter").getAsBoolean().value();
                        String FilterURI = get1.get("FilterURI").getAsString().value();
                        FilterURI = ApplyFilter ? FilterURI : null;
                        
                        boolean DateBoost = get1.get("DateBoost").getAsBoolean().value();
                        String Date = get1.get("Date").getAsString().value();
                        Date = DateBoost ? Date : null;
                        
                        boolean TwoSteps = get1.get("TwoSteps").getAsBoolean().value();
                        String Query2 = get1.get("Query2").getAsString().value();
                        Query2 = TwoSteps ? Query2 : null;
                        
                        Harvester hv = new Harvester(get1.get("Name").getAsString().value(), get1.get("Endpoint").getAsString().value(), get1.get("MainClass").getAsString().value(), get1.get("Query").getAsString().value(), FilterURI, Date, Query2);
                        hv.Harvest();
                        hv.GarbageCollector();
                    } catch (Exception ex) {
                        ex.printStackTrace(new PrintStream(System.out));
                    }
                }
                try {
                    Thread.sleep(1000 * 12 * 60 * 60);
                } catch (Exception ex) {
                }
                
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        }

    }

}
