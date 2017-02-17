/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;



/**
 *
 * @author cedia
 */
public class CacheResults {

    private Cache DiskCache = null;
    
    private CacheResults() {

        CacheManager cacheManager = CacheManager.getInstance();
        int tenDays = 10*24 * 60 * 60;
        DiskCache = new Cache("GeonamesResults", 1000, true, false, tenDays, tenDays);
        cacheManager.addCache(DiskCache);

    }

    public static CacheResults getInstance() {
        return CacheHolder.INSTANCE;
    }

    private static class CacheHolder {

        private static final CacheResults INSTANCE = new CacheResults();
    }

    public Cache getDiskCache() {
        return DiskCache;
    }

    public void setDiskCache(Cache DiskCache) {
        this.DiskCache = DiskCache;
    }
    
    
    
    
}
