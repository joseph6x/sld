/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;

/**
 *
 * @author cedia
 */
public class LinksDemon extends Thread {

    @Override
    public void run() {

        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();

        for (;;) {
            try {
                for (int i = 0; i < get.size(); i++) {
                    JsonObject get1 = get.get(i).getAsObject();
                    String value = ConfigInfo.getInstance().getConfig().get("Output").getAsString().value();
                    String filename = value + get1.get("Name").getAsString().value() + ".nt";
                    PrintWriter writer = new PrintWriter(filename);
                    writer.print("");
                    writer.close();
                }
                for (int i = 0; i < get.size(); i++) {

                    JsonObject get1 = get.get(i).getAsObject();
                    LinksDiscovery hv = new LinksDiscovery(get1.get("Endpoint").getAsString().value(), get1.get("MainClass").getAsString().value(), get1.get("Query").getAsString().value(), get1.get("Name").getAsString().value());
                    hv.Discovery();

                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
            try {
                Thread.sleep(3600 * 1 * 1000);
            } catch (Exception ex) {
            }

        }

    }
}
