package ua.com.jday2015.demo.jcache;

import java.util.Iterator;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

/**
 * Demonstrates some basic cache operations such as put, get, remove, iterate, etc ...
 * 
 * @author Serkan OZAL
 */
public class CacheOperationsDemo {

    private static final String CACHE_NAME = "jday2015";
    
    static {
        System.setProperty("hazelcast.logging.type", "none");
    }
    
    public static void main(String[] args) {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        
        // *************************************************************************** //
        
        // We have not created cache, so lets create it first.
        CompleteConfiguration<Integer, String> cacheConfig = 
                new MutableConfiguration<Integer, String>()
                        // In fact, configuring types is not needed 
                        // if you really don't create type checking for putting into cache.
                        .setTypes(Integer.class, String.class); 
        Cache<Integer, String> cache = cacheManager.createCache(CACHE_NAME, cacheConfig);
        System.out.println("Cache \"" + CACHE_NAME + "\" has been created");
        
        // *************************************************************************** //
        
        final int ENTRY_COUNT = 3;
        
        for (int i = 1; i <= ENTRY_COUNT; i++) {
            cache.put(i + 1, "Value-1");
            System.out.println("Put key \"" + i + "\" with value \"Value-" + i + "\"");
        }
        
        for (int i = 1; i <= ENTRY_COUNT; i++) {
            System.out.println("Get value with key \"" + i + "\": " + cache.get(i));
        }

        System.out.println("Iterating over entries ...");
        Iterator<Cache.Entry<Integer, String>> iter1 = cache.iterator();
        while (iter1.hasNext()) {
            Cache.Entry<Integer, String> entry = iter1.next();
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
        }
        
        cache.put(1, "Value-10");
        System.out.println("Put key \"1\" with value \"Value-10\"");
        
        System.out.println("Get value with key \"1\": " + cache.get(1));
        
        cache.replace(1, "Value-100");
        System.out.println("Replace key \"1\" with value \"Value-100\"");
        
        System.out.println("Get value with key \"1\": " + cache.get(1));
        
        cache.remove(1);
        System.out.println("Remove value with key \"1\"");
        
        System.out.println("Get value with key \"1\": " + cache.get(1));
        
        System.out.println("Iterating over entries ...");
        Iterator<Cache.Entry<Integer, String>> iter2 = cache.iterator();
        while (iter2.hasNext()) {
            Cache.Entry<Integer, String> entry = iter2.next();
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
        }
        
        cache.clear();
        
        System.out.println("Iterating over entries ...");
        Iterator<Cache.Entry<Integer, String>> iter3 = cache.iterator();
        while (iter3.hasNext()) {
            Cache.Entry<Integer, String> entry = iter3.next();
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
        }
        
        // *************************************************************************** //
        
        // Close caching provider. 
        // This also closes all owned cache managers and destroys theirs owned caches.
        cachingProvider.close();
    }

}
