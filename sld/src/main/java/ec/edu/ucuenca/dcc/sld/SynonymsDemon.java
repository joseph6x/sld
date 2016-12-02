/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cedia
 */
public class SynonymsDemon extends Thread {
//dict.1.1.20161202T224400Z.34ac0ab516f65849.e3fe237d6539b089a94dfc9be13825ebca64572f
    @Override
    public void run() {
        for (;;) {

            try {
                SolrConnection instance = SolrConnection.getInstance();
                String[] FindOne = instance.FindOne("state", "0", "originalText", "uri");
            
                if (FindOne==null){
                    Thread.sleep(3600000);
                }else{
                    
                }
                
            
                
            } catch (Exception ex) {
                Logger.getLogger(SynonymsDemon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
