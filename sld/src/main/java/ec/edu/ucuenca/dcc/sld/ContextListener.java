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

    private HarvestingDemon D1 = null;
    private SynonymsDemon D2 = null;
    private CorticalDemon D3 = null;
    private LinksDemon D4 = null;

    public void contextInitialized(ServletContextEvent sce) {
        if ((D1 == null) || (!D1.isAlive())) {
            D1 = new HarvestingDemon();
            D1.start();
        }
        if ((D2 == null) || (!D2.isAlive())) {
            D2 = new SynonymsDemon();
            D2.start();
        }
        if ((D3 == null) || (!D3.isAlive())) {
            D3 = new CorticalDemon();
            D3.start();
        }
        if ((D4 == null) || (!D4.isAlive())) {
            D4 = new LinksDemon();
            D4.start();
        }
        try {
            Cache instance = Cache.getInstance();
            instance.get("");
        } catch (Exception ex) {
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            D1.interrupt();
            D2.interrupt();
            D3.interrupt();
            D4.interrupt();
            Cache instance = Cache.getInstance();
            instance.Kill();
        } catch (Exception ex) {
        }
    }

}
