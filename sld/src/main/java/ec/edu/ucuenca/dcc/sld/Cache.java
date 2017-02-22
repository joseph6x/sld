/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cedia
 */
public class Cache {

    // JDBC driver name and database URL
    String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_URL = "jdbc:mysql://localhost:3306/PentahoUtil";
    //  Database credentials
    String USER = "root";
    String PASS = "cedia";
    Connection conn = null;

    private Cache() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void put(String key, String value) throws SQLException {
        PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO Cache (Cache.key, value) values (?, ?)");
        stmt2.setString(1, getMD5(key));
        stmt2.setString(2, value);
        stmt2.executeUpdate();
        stmt2.close();
    }

    public synchronized String get(String key) throws SQLException {
        String sql = "SELECT * FROM Cache where Cache.key='" + getMD5(key) + "'";
        Statement stmt = conn.createStatement();
        java.sql.ResultSet rs = stmt.executeQuery(sql);
        String DataResult = null;
        if (rs.next()) {
            DataResult = rs.getString("value");
        }
        rs.close();
        stmt.close();
        return DataResult;
    }

    public static Cache getInstance() {
        return CacheHolder.INSTANCE;
    }

    private static class CacheHolder {

        private static final Cache INSTANCE = new Cache();
    }

    public String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
