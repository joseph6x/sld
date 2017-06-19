/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.PrintStream;
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
            for (int i = 0; i < get.size(); i++) {
                try {
                    JsonObject get1 = get.get(i).getAsObject();
                    boolean ApplyFilter = get1.get("ApplyFilter").getAsBoolean().value();
                    String FilterURI = get1.get("FilterURI").getAsString().value();
                    FilterURI = ApplyFilter ? FilterURI : null;

                    Harvester hv = new Harvester(get1.get("Name").getAsString().value(), get1.get("Endpoint").getAsString().value(), get1.get("MainClass").getAsString().value(), get1.get("Query").getAsString().value(), FilterURI);
                    hv.Harvest();
                } catch (Exception ex) {
                    ex.printStackTrace(new PrintStream(System.out));
                }
            }
            try {
                Thread.sleep(1000 * 12 * 60 * 60);
            } catch (Exception ex) {
            }

        }

    }

}
