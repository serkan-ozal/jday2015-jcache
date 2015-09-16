package ua.com.jday2015.demo.jcache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;
import javax.cache.spi.CachingProvider;

/**
 * Demonstrates using cache entry listener to be notified for cache events 
 * such as adding, updating, removing to/from cache.
 * 
 * @author Serkan OZAL
 */
public class CacheEntryListenerDemo {

    private static final String CACHE_NAME = "jday2015";
    
    static {
        System.setProperty("hazelcast.logging.type", "none");
    }
    
    public static void main(String[] args) throws InterruptedException {
        final int EXPIRATION_TIME_IN_SECONDS = 3;
        
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        
        // *************************************************************************** //
      
        // We have not created cache, so lets create it first.
        CompleteConfiguration<Integer, String> cacheConfig = 
                new MutableConfiguration<Integer, String>()
                        .addCacheEntryListenerConfiguration(new DemoCacheEntryListenerConfiguration())
                        // 3 seconds later after latest touch (creation, update or access), 
                        // records are considered as expired
                        .setExpiryPolicyFactory(
                                FactoryBuilder.factoryOf(
                                        new TouchedExpiryPolicy(new Duration(TimeUnit.SECONDS, EXPIRATION_TIME_IN_SECONDS))))
                        // In fact, configuring types is not needed 
                        // if you really don't create type checking for putting into cache.
                        .setTypes(Integer.class, String.class); 
        Cache<Integer, String> cache = cacheManager.createCache(CACHE_NAME, cacheConfig);
        System.out.println("Cache \"" + CACHE_NAME + "\" has been created");
        
        // *************************************************************************** //
        
        // Add a new entry so this will trigger "CREATE" event
        cache.put(1, "Value-1");
        System.out.println("Put key \"1\" with value \"Value-1\"");
        
        // Add a new entry so this will trigger "CREATE" event
        cache.put(2, "Value-2");
        System.out.println("Put key \"1\" with value \"Value-1\"");
        
        // Update an entry so this will trigger "UPDATE" event
        cache.put(1, "Value-10");
        System.out.println("Put key \"1\" with value \"Value-10\"");
        
        // Remove an entry so this will trigger "REMOVE" event
        cache.remove(2);
        System.out.println("Remove value with key \"2\"");
        
        // Wait for entry expired 
        Thread.sleep((EXPIRATION_TIME_IN_SECONDS + 1) * 1000);
        
        // When accessing to an expired entry, entry will be expired and this  will trigger "EXPIRED" event
        System.out.println("Get value with key \"1\": " + cache.get(1) + " [must be null]");
        
        // *************************************************************************** //
        
        // Close caching provider. 
        // This also closes all owned cache managers and destroys theirs owned caches.
        cachingProvider.close();
    }
    
    @SuppressWarnings("serial")
    private static class DemoCacheEntryListenerConfiguration 
            implements CacheEntryListenerConfiguration<Integer, String> {
        
        @Override
        public boolean isSynchronous() {
            // Get events synchronously
            return true;
        }
        
        @Override
        public boolean isOldValueRequired() {
            // Give me also old value
            return true;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public Factory<CacheEntryListener<? super Integer, ? super String>> getCacheEntryListenerFactory() {
            return (Factory<CacheEntryListener<? super Integer, ? super String>>)
                    ((Object) FactoryBuilder.factoryOf(new DemoCacheEntryListener()));
        }
        
        @Override
        public Factory<CacheEntryEventFilter<? super Integer, ? super String>> getCacheEntryEventFilterFactory() {
            // No need to filtering, get all events
            return null;
        }
    }
    
    @SuppressWarnings("serial")
    private static class DemoCacheEntryListener 
        implements  Serializable,
                    CacheEntryCreatedListener<Integer, String>,
                    CacheEntryUpdatedListener<Integer, String>, 
                    CacheEntryRemovedListener<Integer, String>,   
                    CacheEntryExpiredListener<Integer, String> {

        @Override
        public void onExpired(Iterable<CacheEntryEvent<? extends Integer, ? extends String>> events)
                throws CacheEntryListenerException {
            for (CacheEntryEvent<? extends Integer, ? extends String> event : events) {
                System.out.println("[onExpired] " + entryEventToString(event));
            }    
        }

        @Override
        public void onRemoved(Iterable<CacheEntryEvent<? extends Integer, ? extends String>> events)
                throws CacheEntryListenerException {
            for (CacheEntryEvent<? extends Integer, ? extends String> event : events) {
                System.out.println("[onRemoved] " + entryEventToString(event));
            } 
        }

        @Override
        public void onUpdated(Iterable<CacheEntryEvent<? extends Integer, ? extends String>> events)
                throws CacheEntryListenerException {
            for (CacheEntryEvent<? extends Integer, ? extends String> event : events) {
                System.out.println("[onUpdated] " + entryEventToString(event));
            } 
        }

        @Override
        public void onCreated(Iterable<CacheEntryEvent<? extends Integer, ? extends String>> events)
                throws CacheEntryListenerException {
            for (CacheEntryEvent<? extends Integer, ? extends String> event : events) {
                System.out.println("[onCreated] " + entryEventToString(event));
            } 
        }
        
        private String entryEventToString(CacheEntryEvent<? extends Integer, ? extends String> event) {
            return  "key: " + event.getKey() + ", " + 
                    "value: " + event.getValue() + ", " +
                    "old-value: " + event.getOldValue();
        }
        
    }

}
