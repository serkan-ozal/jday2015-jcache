package ua.com.jday2015.demo.jcache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import javax.cache.spi.CachingProvider;

/**
 * Demonstrates using cache entry processor to execute your logic 
 * on cache entries by updating or removing them.
 * 
 * @author Serkan OZAL
 */
public class CacheEntryProcessorDemo {

    private static final String CACHE_NAME = "jday2015";
    
    static {
        System.setProperty("hazelcast.logging.type", "none");
    }
    
    public static void main(String[] args) throws InterruptedException {
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
        
        final int ENTRY_COUNT = 10;
        
        for (int i = 1; i <= ENTRY_COUNT; i++) {
            cache.put(i, "Value-" + i);
            System.out.println("Put key \"" + i + "\" with value \"Value-" + i + "\"");
        }
        
        for (int i = 1; i <= ENTRY_COUNT; i++) {
            System.out.println("Invoke entry processor on key " + "\"" + i + "\"" + " and the result is: " + 
                               cache.invoke(i, new DemoCacheEntryProcessor(), new Object[] {}));
        }
        
        for (int i = 1; i <= ENTRY_COUNT; i++) {
            System.out.println("Get value with key \"" + i + "\": " + cache.get(i));
        }
        
        // *************************************************************************** //
        
        // Close caching provider. 
        // This also closes all owned cache managers and destroys theirs owned caches.
        cachingProvider.close();
    }
    
    private static class DemoCacheEntryProcessor 
            implements EntryProcessor<Integer, String, String> {

        @Override
        public String process(MutableEntry<Integer, String> entry,
                Object... arguments) throws EntryProcessorException {
            String newValue = null;
            if (entry.getKey() % 2 == 0) {
                newValue = entry.getValue() + " is value of an even number";
            } else {
                newValue = entry.getValue() + " is value of an odd number";
            }
            entry.setValue(newValue);
            return newValue;
        }

    }

}
