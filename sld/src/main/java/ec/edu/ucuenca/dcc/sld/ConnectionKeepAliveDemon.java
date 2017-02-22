/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cedia
 */
public class ConnectionKeepAliveDemon extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                Cache instance = Cache.getInstance();
                String get = instance.get("hola");
                Thread.sleep(1000 * 60);
            } catch (Exception ex) {
                Logger.getLogger(ConnectionKeepAliveDemon.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void interrupt() {

    }

}
