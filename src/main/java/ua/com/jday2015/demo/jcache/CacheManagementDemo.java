package ua.com.jday2015.demo.jcache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

/**
 * Demonstrates cache management operations such as access, create and destroy.
 * 
 * @author Serkan OZAL
 */
public class CacheManagementDemo {

    private static final String CACHE_NAME = "jday2015";
    
    static {
        System.setProperty("hazelcast.logging.type", "none");
    }
    
    public static void main(String[] args) {
        // Get the all existing JCache API implementation in the classpath. 
        for (CachingProvider cachingProvider : Caching.getCachingProviders()) {
            System.out.println("JCache API Implementation:");
            System.out.println("\t- Default URI     : " + cachingProvider.getDefaultURI()); 
            System.out.println("\t- CachingProvider : " + cachingProvider);
        }
        
        // *************************************************************************** //
        
        // Get the JCache API implementation (we have only one in the classpath).
        // If there would be multiple, we will be smashed to our face with "Multiple CachingProviders" exception.
        CachingProvider cachingProvider = Caching.getCachingProvider();
        
        // Ask the JCache API to give us Hazelcast's JCache implementation.
        // This can be useful if there are multiple JCache implementations in the classpath.
        CachingProvider hazelcastCachingProvider = 
                Caching.getCachingProvider("com.hazelcast.cache.HazelcastCachingProvider");
        System.out.println("Hazelcast's CachingProvider: " + hazelcastCachingProvider);
        
        // *************************************************************************** //
        
        // Get the cache manager which represents cache scopes over JCache implementation.
        // There can be multiple cache managers (scopes) with different URI and classloader 
        // over same JCache implementation via "CachingProvider#getCacheManager(URI uri, ClassLoader classLoader)".
        CacheManager cacheManager = cachingProvider.getCacheManager();
        
        // *************************************************************************** //
        
        Cache<Integer, String> cache = cacheManager.getCache(CACHE_NAME);
        System.out.println("Cache \"" + CACHE_NAME + "\": " + cache);
        
        // *************************************************************************** //
        
        // We have not created cache, so lets create it first.
        CompleteConfiguration<Integer, String> cacheConfig = 
                new MutableConfiguration<Integer, String>()
                        // In fact, configuring types is not needed 
                        // if you really don't create type checking for putting into cache.
                        .setTypes(Integer.class, String.class); 
        cache = cacheManager.createCache(CACHE_NAME, cacheConfig);
        System.out.println("Cache \"" + CACHE_NAME + "\" has been created");
        System.out.println("Cache \"" + CACHE_NAME + "\": " + cache);
        
        // *************************************************************************** //
        
        // Destroy cache (clear all its resources such as data, listeners, etc ...) 
        cacheManager.destroyCache(CACHE_NAME);
        System.out.println("Cache \"" + CACHE_NAME + "\" has been destroyed!");
        
        // *************************************************************************** //
        
        cache = cacheManager.getCache(CACHE_NAME);
        System.out.println("Cache \"" + CACHE_NAME + "\": " + cache);
        
        // *************************************************************************** //
        
        // Close caching provider. 
        // This also closes all owned cache managers and destroys theirs owned caches.
        cachingProvider.close();
    }

}
