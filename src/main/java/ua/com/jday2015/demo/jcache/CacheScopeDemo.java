package ua.com.jday2015.demo.jcache;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

import com.hazelcast.cache.HazelcastCachingProvider;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * Demonstrates using caches in scoped environments.
 * 
 * @author Serkan OZAL
 */
public class CacheScopeDemo {

    private static final String CACHE_NAME = "jday2015";
    
    public static void main(String[] args) throws URISyntaxException {
        // Get the JCache API implementation (we have only one in the classpath).
        CachingProvider cachingProvider = Caching.getCachingProvider();

        // *************************************************************************** //
        
        Properties properties = initialize();
        
        // URI for "Turkey" scope
        final URI TR_URI = new URI("Turkey");
        
        // URI for "Ukraine" scope
        final URI UA_URI = new URI("Ukraine");
        
        // Get the cache manager scoped with "Turkey" URI, default classloader and given properties.
        CacheManager cacheManagerTR = cachingProvider.getCacheManager(TR_URI, null, properties);
        
        // Get the cache manager scoped with "Ukraine" URI, default classloader and given properties.
        CacheManager cacheManagerUA = cachingProvider.getCacheManager(UA_URI, null, properties);
        
        // *************************************************************************** //
        
        // We have not created cache, so lets create it first.
        CompleteConfiguration<Integer, String> cacheConfig = 
                new MutableConfiguration<Integer, String>()
                        // In fact, configuring types is not needed 
                        // if you really don't create type checking for putting into cache.
                        .setTypes(Integer.class, String.class); 
        
        // Create a cache specific to "Turkey" URI scope.
        Cache<Integer, String> cacheTR = cacheManagerTR.createCache(CACHE_NAME, cacheConfig);
        
        // Create a cache specific to "Ukraine" URI scope.
        Cache<Integer, String> cacheUA = cacheManagerUA.createCache(CACHE_NAME, cacheConfig);
        
        System.out.println("Cache \"" + CACHE_NAME + "\" from \"" + TR_URI + "\" URI: " + cacheTR);
        System.out.println("Cache \"" + CACHE_NAME + "\" from \"" + UA_URI + "\" URI: " + cacheUA);
        
        System.out.println("As seen, there are different cache with same cache name.");
        System.out.println("Because their scopes (URI) are different.");
  
        // *************************************************************************** //
        
        // Close caching provider. 
        // This also closes all owned cache managers and destroys theirs owned caches.
        cachingProvider.close();
    }
    
    private static Properties initialize() {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        Properties properties = new Properties();
        properties.put(HazelcastCachingProvider.HAZELCAST_INSTANCE_NAME, hazelcastInstance.getName());
        return properties;
    }

}
