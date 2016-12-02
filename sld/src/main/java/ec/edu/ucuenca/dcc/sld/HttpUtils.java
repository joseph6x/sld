/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author cedia
 */
public class HttpUtils {

    public static synchronized String Http(String s) throws SQLException, IOException {

        String resp = "";
        final URL url = new URL(s);
        final URLConnection connection = url.openConnection();
        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:44.0) Gecko/20100101 Firefox/44.0");
        connection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        final Scanner reader = new Scanner(connection.getInputStream(), "UTF-8");
        while (reader.hasNextLine()) {
            final String line = reader.nextLine();
            resp += line + "\n";
        }
        reader.close();

        return resp;
    }

}
