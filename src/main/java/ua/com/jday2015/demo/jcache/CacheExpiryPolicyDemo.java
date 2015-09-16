package ua.com.jday2015.demo.jcache;

import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

/**
 * Demonstrates using expiry policies in for expiring entries.
 * 
 * @author Serkan OZAL
 */
public class CacheExpiryPolicyDemo {

    private static final String CACHE_NAME = "jday2015";
    
    static {
        System.setProperty("hazelcast.logging.type", "none");
    }
    
    public static void main(String[] args) throws InterruptedException {
        final int EXPIRATION_TIME_IN_SECONDS = 3;
        
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        
        // *************************************************************************** //
        
        /*
         * Here are built-in expiry policies:
         * 
         *      - AccessedExpiryPolicy : The expiry of a entry based on the last time it was accessed.
         *      - CreatedExpiryPolicy  : The expiry of a entry based on when it was created.
         *      - EternalExpiryPolicy  : Specifies that entries won't expire.
         *      - ModifiedExpiryPolicy : The expiry of an entry based on the last time it was updated.
         *      - TouchedExpiryPolicy  : The expiry of an entry based on when it was last touched. 
         *                               A touch includes creation, update or access.
         */
        
        // We have not created cache, so lets create it first.
        CompleteConfiguration<Integer, String> cacheConfig = 
                new MutableConfiguration<Integer, String>()
                        // 3 seconds later after creation, records are considered as expired
                        .setExpiryPolicyFactory(
                                FactoryBuilder.factoryOf(
                                        new CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS, EXPIRATION_TIME_IN_SECONDS))))
                        // In fact, configuring types is not needed 
                        // if you really don't create type checking for putting into cache.
                        .setTypes(Integer.class, String.class); 
        Cache<Integer, String> cache = cacheManager.createCache(CACHE_NAME, cacheConfig);
        System.out.println("Cache \"" + CACHE_NAME + "\" has been created");
        
        // *************************************************************************** //
        
        cache.put(1, "Value-1");
        System.out.println("Put key \"1\" with value \"Value-1\"");
        
        System.out.println("Get value with key \"1\": " + cache.get(1));
        
        // Wait for entry expired 
        Thread.sleep((EXPIRATION_TIME_IN_SECONDS + 1) * 1000);
        
        System.out.println("Get value with key \"1\": " + cache.get(1) + " [must be null]");
        
        // *************************************************************************** //
        
        // Close caching provider. 
        // This also closes all owned cache managers and destroys theirs owned caches.
        cachingProvider.close();
    }

}
