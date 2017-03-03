/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.mapdb.serializer.SerializerCompressionWrapper;

/**
 *
 * @author cedia
 */
public class Cache {

    DB db = null;
    HTreeMap<String, String> create =null;
    
    
    private Cache() {
        db = DBMaker.fileDB("/home/cedia/cache.db").make();
        create = db.hashMap("cache", Serializer.STRING, new SerializerCompressionWrapper(Serializer.STRING)).createOrOpen();
    }

    public void put(String key, String value)  {
        create.put(key, value);
    }

    public void Kill() throws SQLException {
        create.close();
        db.close();
    }

    public String get(String key) {

        String get = create.get(key);
        
        return get;
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
