package ua.com.jday2015.demo.jcache;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.cache.Cache;
import javax.cache.Cache.Entry;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CacheWriterException;
import javax.cache.spi.CachingProvider;

/**
 * Demonstrates using cache loader to load data from your back-end 
 * and write data to your back-end.
 * 
 * @author Serkan OZAL
 */
public class CacheLoaderWriterDemo {

    public static final String CACHE_NAME = "jday2015";
    
    private static final Map<Integer, String> BACKEND = new ConcurrentHashMap<Integer, String>();
    private static final int ENTRY_COUNT = 100;
    
    static {
        System.setProperty("hazelcast.logging.type", "none");
        init();
    }
    
    private static void init() {
        for (int i = 1; i <= ENTRY_COUNT; i++) {
            BACKEND.put(i, "Value-" + i);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        
        // *************************************************************************** //
      
        // We have not created cache, so lets create it first.
        CompleteConfiguration<Integer, String> cacheConfig = 
                new MutableConfiguration<Integer, String>()
                        .setReadThrough(true)
                        .setCacheLoaderFactory(FactoryBuilder.factoryOf(DemoCacheLoader.class))
                        .setWriteThrough(true)
                        .setCacheWriterFactory(FactoryBuilder.factoryOf(DemoCacheWriter.class))
                        // In fact, configuring types is not needed 
                        // if you really don't create type checking for putting into cache.
                        .setTypes(Integer.class, String.class); 
        Cache<Integer, String> cache = cacheManager.createCache(CACHE_NAME, cacheConfig);
        System.out.println("Cache \"" + CACHE_NAME + "\" has been created");
        
        // *************************************************************************** //
        
        for (int i = 1; i <= ENTRY_COUNT; i++) {
            System.out.println("Get value with key \"" + i + "\": " + cache.get(i));
        }
        
        for (int i = ENTRY_COUNT + 1; i <= 2 * ENTRY_COUNT; i++) {
            cache.put(i, "Value-" + i);
            System.out.println("Put key \"" + i + "\" with value \"Value-" + i + "\"");
        }
        
        System.out.println("Entries in backend: ");
        for (Map.Entry<Integer, String> entry : BACKEND.entrySet()) {
            System.out.println("\tKey: " + entry.getKey() + ", " + "Value: " + entry.getValue());
        }
        
        for (int i = 1; i <= ENTRY_COUNT; i++) {
            cache.remove(i);
            System.out.println("Remove value with key \"" + i);
        }

        System.out.println("Entries in backend: ");
        for (Map.Entry<Integer, String> entry : BACKEND.entrySet()) {
            System.out.println("\tKey: " + entry.getKey() + ", " + "Value: " + entry.getValue());
        }
        
        // *************************************************************************** //
        
        // Close caching provider. 
        // This also closes all owned cache managers and destroys theirs owned caches.
        cachingProvider.close();
    }
    
    @SuppressWarnings("serial")
    public static class DemoCacheLoader 
            implements CacheLoader<Integer, String>, Serializable {

        @Override
        public String load(Integer key) throws CacheLoaderException {
            return BACKEND.get(key);
        }

        @Override
        public Map<Integer, String> loadAll(Iterable<? extends Integer> keys)
                throws CacheLoaderException {
            Map<Integer, String> loadedKeyAndValues = new HashMap<Integer, String>();
            for (Integer key : keys) {
                loadedKeyAndValues.put(key, BACKEND.get(key));
            }
            return loadedKeyAndValues;
        }

    }
    
    public static class DemoCacheWriter 
            implements CacheWriter<Integer, String> {

        @Override
        public void write(Entry<? extends Integer, ? extends String> entry)
                throws CacheWriterException {
            BACKEND.put(entry.getKey(), entry.getValue());
        }

        @Override
        public void writeAll(Collection<Entry<? extends Integer, ? extends String>> entries)
                throws CacheWriterException {
            for (Entry<? extends Integer, ? extends String> entry : entries) {
                BACKEND.put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void delete(Object key) throws CacheWriterException {
            BACKEND.remove(key);
        }

        @Override
        public void deleteAll(Collection<?> keys) throws CacheWriterException {
            for (Object key : keys) {
                BACKEND.remove(key);
            }
        }
        
    }

}
