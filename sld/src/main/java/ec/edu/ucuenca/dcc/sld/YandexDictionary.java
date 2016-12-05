/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;

/**
 *
 * @author cedia
 */
public class YandexDictionary {

    public static List<String> Syn(String txt, int n) throws SQLException, IOException {

        try {
            String Http = HttpUtils.Http("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=dict.1.1.20161202T224400Z.34ac0ab516f65849.e3fe237d6539b089a94dfc9be13825ebca64572f&lang=en-en&text=" + URLEncoder.encode(txt, "UTF-8"));

            List<String> ls = new ArrayList<String>();
            JsonObject parse = JSON.parse(Http);
            JsonArray asArray = parse.get("def").getAsArray();
            for (int i = 0; i < asArray.size(); i++) {
                JsonArray asArray1 = asArray.get(i).getAsObject().get("tr").getAsArray();
                for (int j = 0; j < asArray1.size(); j++) {

                    if (asArray1.get(j).getAsObject().get("syn") != null) {
                        JsonArray asArray2 = asArray1.get(j).getAsObject().get("syn").getAsArray();
                        for (int k = 0; k < asArray2.size(); k++) {
                            String value = asArray2.get(k).getAsObject().get("text").getAsString().value();
                            ls.add(value);
                        }
                    }
                }
            }

            return ls.size() <= n ? ls : ls.subList(0, n);
        } catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));

            return null;
        }

    }

}
