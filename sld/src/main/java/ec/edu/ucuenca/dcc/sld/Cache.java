/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import com.google.common.cache.CacheBuilder;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author cedia
 */
public class Cache {

  com.google.common.cache.Cache<String, String> build;
  List<String> BlackList = new ArrayList();

  private Cache() {
    build = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(24, TimeUnit.HOURS).build();
  }

  public void put(String key, String value) {
    build.asMap().put(key, value);
  }

  public void Kill() throws SQLException {
    build.cleanUp();
  }

  public String get(String key) {
    return build.asMap().get(key);
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
