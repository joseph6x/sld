/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.PrintStream;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;

/**
 *
 * @author cedia
 */
public class CorticalAPI {

    public static int SemanticDistance(String word1, String word2) throws Exception {

       // System.out.println("Comparanado " + word1 + "-" + word2);
        //    String example = "[ { \"text\": \""+ word1+"\" },{ \"text\": \""+word2+"\"} ]";
        String example = "[ { \"text\": \"" + word1 + "\" },{ \"text\": \"" + word2 + "\"} ]";
        String result = HttpUtils.sendPost2("compare", example);
        //System.out.print ("");
        try {
           // System.out.println(result);
            
            JsonObject asObject = JSON.parse(result).getAsObject();

            //String ws = asObject.get("weightedScoring").getAsNumber()..toString();
            //String over = asObject.get("overlappingAll").toString();
            int intValue = asObject.get("overlappingAll").getAsNumber().value().intValue();
            
            ///  contenedor = agregar (iduser,fav , contenedor);
            return intValue;
        } catch (Exception ex) {
            ex.printStackTrace(new PrintStream(System.out));
            return 0;
        }

    }

}
