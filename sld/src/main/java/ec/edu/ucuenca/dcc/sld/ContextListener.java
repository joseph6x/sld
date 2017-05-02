/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author cedia
 */
public class ContextListener implements ServletContextListener {

    //private HarvestingDemon D1 = null;
    // private ConnectionKeepAliveDemon Demon = null;
    public void contextInitialized(ServletContextEvent sce) {
        // if ((D1 == null) || (!D1.isAlive())) {
        //      D1 = new HarvestingDemon();
        ////     D1.start();
        //   }
        //   if ((Demon == null) || (!Demon.isAlive())) {
        // Demon = new ConnectionKeepAliveDemon();
        // Demon.start();

        try {
            Cache instance = Cache.getInstance();

            instance.get("");

            FedXSPARQL instance1 = FedXSPARQL.getInstance();

        } catch (Exception ex) {
        }

        //  }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            //D1.interrupt();
            // Demon.interrupt();
            Cache instance = Cache.getInstance();

            instance.Kill();

            FedXSPARQL instance1 = FedXSPARQL.getInstance();

            instance1.Kill();

        } catch (Exception ex) {
        }
    }

}
