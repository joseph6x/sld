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


    public void contextInitialized(ServletContextEvent sce) {
       // if ((D1 == null) || (!D1.isAlive())) {
      //      D1 = new HarvestingDemon();
       ////     D1.start();
     //   }

    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            //D1.interrupt();

        } catch (Exception ex) {
        }
    }

}
