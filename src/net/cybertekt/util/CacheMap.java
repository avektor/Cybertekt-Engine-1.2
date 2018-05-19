package net.cybertekt.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache Map - (C) Cybertekt Software
 *
 * -Null keys are not permitted.
 * -Not synchronized for performance reasons.
 * -Cache is updated automatically during method calls, but may be updated manually by calling update().
 *
 * @version 1.2.0
 * @author Andrew Vektor
 *
 * @param <Key>
 * @param <Value>
 */
public class CacheMap<Key, Value> {

    /**
     * Static SLF4J Class Logger for Debugging.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CacheMap.class);

    /**
     * Determines the type of {@link java.util.Map} used internally for storing
     * and retrieving cached key-value pairs.
     */
    public static enum CacheType {
        /**
         * Cached key-value pairs are stored internally using a
         * {@link java.util.concurrent.ConcurrentHashMap}.
         */
        Concurrent,
        /**
         * Cached key-value pairs are stored internally using a
         * {@link java.util.IdentityHashMap}.
         */
        Identity,
        /**
         * Cached key-value pairs are stored internally using a
         * {@link java.util.HashMap}.
         */
        Hash;
    }

    /**
     * Determines how cached key-value pairs are evicted from the cache.
     */
    public static enum CacheMode {
        /**
         * Values are cached using {@link SoftReference soft references} and are
         * enqueued for removal from the cache when memory is constrained and no
         * strong references to the value exist.
         */
        Soft,
        /**
         * Values are cached using {@link WeakReference weak references} and are
         * enqueued for removal from the cache when no strong or soft references
         * to the value exist.
         */
        Weak;
    }

    /**
     * Determines when a cached key-value pairs will be considered obsolete and
     * enqueued for removal from the cache map.
     */
    private final CacheMode MODE;

    private final Map<Key, Reference<Value>> CACHE;

    private final ReferenceQueue<? extends Value> QUEUE;

    public CacheMap() {
        this(CacheType.Hash, CacheMode.Weak);
    }
    
    public CacheMap(final CacheMode cacheMode) {
        this(CacheType.Hash, cacheMode);
    }
    
    public CacheMap(final CacheType cacheType) {
        this(cacheType, CacheMode.Weak);
    }
    
    public CacheMap(final CacheType cacheType, final CacheMode cacheMode) {
        MODE = cacheMode;
        CACHE = createCache(cacheType);
        QUEUE = new ReferenceQueue<>();
    }

    public final Key put(final Key KEY, final Value VALUE) {
        update();
        Reference<Value> last;
        if ((last = CACHE.put(KEY, createReference(KEY, VALUE))) != null) {
            ((CacheReference<Key>) last).clear();
        }
        return KEY;
    }

    public final Value get(final Key KEY) {
        update();
        Reference<Value> ref = CACHE.get(KEY);
        return (ref != null) ? ref.get() : null;
    }

    public void remove(final Key KEY) {
        Reference<Value> ref = CACHE.remove(KEY);
        if (ref != null) {
            ((CacheReference<Key>) ref).clear();
        }
        update();
    }

    public final void clear() {
        for (Reference<Value> ref : CACHE.values()) {
            ((CacheReference<Key>) ref).clear();
        }
        CACHE.clear();
        update();
    }
    
    public final boolean contains(final Key KEY) {
        update();
        return CACHE.containsKey(KEY);
    }
    
    public final int size() {
        update();
        return CACHE.size();
    }

    public final void update() {
        Key key;
        for (Reference<? extends Value> ref = QUEUE.poll(); ref != null; ref = QUEUE.poll()) {
            if ((key = ((CacheReference<Key>) ref).getKey()) != null) {
                CACHE.remove(key);
            }
        }
    }

    /**
     * Constructs the appropriate {@link java.util.Map} for the specified
     * {@link CacheType cache type}.
     *
     * @param TYPE the {@link CacheType cache type} that determines the type of
     * {@link java.util.Map} to be constructed and returned.
     * @return a new {@link java.util.Map} associated with the specified
     * {@link CacheType cache type}.
     */
    private Map<Key, Reference<Value>> createCache(final CacheType TYPE) {
        switch (TYPE) {
            case Identity: {
                return new IdentityHashMap<>();
            }
            case Concurrent: {
                return new ConcurrentHashMap<>();
            }
            case Hash: {
                return new HashMap<>();
            }
            default: {
                throw new IllegalArgumentException("Cache map initialization failed - unrecognized cache type.");
            }
        }
    }

    /**
     * Constructs a {@link CachReference cache reference} for the specified
     * key-value pair. The type of cache reference returned is determined by the
     * {@link CacheMode cache mode} specified during construction. The reference
     * is automatically added to the internal {@link #QUEUE cache queue} when
     * this method is called.
     *
     * @param KEY the key associated with the cache value.
     * @param VALUE the cache value/referent.
     * @return the {@link CacheReference cache reference} constructed for the
     * specified key-pair value.
     */
    private Reference<Value> createReference(final Key KEY, final Value VALUE) {
        switch (MODE) {
            case Soft: {
                return new SoftCacheReference(KEY, VALUE, QUEUE);
            }
            case Weak: {
                return new WeakCacheReference(KEY, VALUE, QUEUE);
            }
            default: {
                throw new IllegalArgumentException("Unable to create cache reference - unrecognized cache mode.");
            }
        }
    }

    private interface CacheReference<Key> {

        /**
         * Returns the key associated with the cached value/referent.
         *
         * @return the key for the cached value/referent.
         */
        public Key getKey();

        /**
         * Clears the referent and its associated key. This should be called
         * when a object in the cache has been replaced by a new object that
         * shares the same key. This prevents overwritten objects from
         * triggering the removal of an object that has replaced it.
         */
        public void clear();

    }

    private class SoftCacheReference extends SoftReference<Value> implements CacheReference<Key> {

        /**
         * The key associated with the cached referent.
         */
        private Key key;

        public SoftCacheReference(final Key KEY, final Value REFERENT, final ReferenceQueue QUEUE) {
            super(REFERENT, QUEUE);
            key = KEY;
        }

        @Override
        public final Key getKey() {
            return key;
        }

        @Override
        public final void clear() {
            super.clear();
            key = null;
            super.enqueue();
        }
    }

    private class WeakCacheReference extends WeakReference<Value> implements CacheReference<Key> {

        /**
         * The key associated with the cached referent.
         */
        private Key key;

        public WeakCacheReference(final Key KEY, final Value REFERENT, final ReferenceQueue QUEUE) {
            super(REFERENT, QUEUE);
            key = KEY;
        }

        @Override
        public final Key getKey() {
            return key;
        }

        @Override
        public final void clear() {
            super.clear();
            key = null;
            super.enqueue();
        }
    }
}
