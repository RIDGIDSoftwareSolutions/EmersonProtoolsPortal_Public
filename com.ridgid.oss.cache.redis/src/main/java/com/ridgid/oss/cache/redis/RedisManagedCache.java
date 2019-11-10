package com.ridgid.oss.cache.redis;

import com.ridgid.oss.common.cache.ManagedCache;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class RedisManagedCache<K, V> implements ManagedCache<K, V> {
    private final RedissonClient client;
    private final String cacheName;
    private final short maxCapacity;
    private final short evictToCapacity;
    private final Timer cleanupTimer;

    private Thread cleanupThread;

    public RedisManagedCache(RedissonClient client,
                             String cacheName,
                             short maxCapacity,
                             short evictToCapacity) {
        this(client, cacheName, (short) 0, maxCapacity, evictToCapacity);
    }

    public RedisManagedCache(RedissonClient client,
                             String cacheName,
                             short timeoutCheckIntervalSeconds,
                             short maxCapacity,
                             short evictToCapacity) {
        this.client = client;
        this.cacheName = cacheName;
        this.maxCapacity = maxCapacity;
        this.evictToCapacity = evictToCapacity;
        this.cleanupTimer = makeCleanupTimer(timeoutCheckIntervalSeconds);
    }

    private Timer makeCleanupTimer(short timeoutCheckIntervalSeconds) {
        Timer timer = new Timer(true);
        if (timeoutCheckIntervalSeconds > 0) {
            timer.schedule(
                    makeCleanupTask(),
                    timeoutCheckIntervalSeconds * 1000,
                    timeoutCheckIntervalSeconds * 1000
            );
        }
        return timer;
    }

    private TimerTask makeCleanupTask() {
        return new TimerTask() {
            @Override
            public void run() {
                cleanup();
            }
        };
    }

    private void cleanup() {
        synchronized (cleanupTimer) {
            if (cleanupThread != null) return;
            cleanupThread = Thread.currentThread();
        }
        try {
            performCleanup();
        } finally {
            synchronized (cleanupTimer) {
                if (cleanupThread == Thread.currentThread())
                    cleanupThread = null;
            }
        }
    }

    private void performCleanup() {
        if (size() > maxCapacity)
            getCache().entrySet().stream()
                    .filter(entry -> size() > evictToCapacity)
                    .forEach(entry -> this.remove(entry.getKey()));
    }

    @Override
    public void forceCleanup() {
        checkCapacity();
    }

    private void checkCapacity() {
        if (size() > evictToCapacity)
            cleanupTimer.schedule(
                makeCleanupTask(),
                10
            );
    }

    private RMap<K, V> getCache() {
        return client.getMap(cacheName);
    }

    @Override
    public int size() {
        return getCache().size();
    }

    @Override
    public void clear() {
        getCache().clear();
    }

    @Override
    public boolean isEmpty() {
        return getCache().isEmpty();
    }

    @Override
    public boolean containsKey(K key) {
        return getCache().containsKey(key);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        getCache().forEach(action);
    }

    @Override
    public Stream<Map.Entry<K, V>> stream() {
        return getCache().entrySet().stream();
    }

    @Override
    public Stream<K> streamKeys() {
        return getCache().keySet().stream();
    }

    @Override
    public Stream<V> streamValues() {
        return getCache().values().stream();
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        return getCache().getOrDefault(key, defaultValue);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return getCache().computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return getCache().computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return getCache().compute(key, remappingFunction);
    }

    @Override
    public V put(K key, V value) {
        return getCache().put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        getCache().putAll(m);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return getCache().putIfAbsent(key, value);
    }

    @Override
    public V remove(K key) {
        return getCache().remove(key);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return getCache().replace(key, oldValue, newValue);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        getCache().replaceAll(function);
    }

    @Override
    public V replace(K key, V value) {
        return getCache().replace(key, value);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return getCache().merge(key, value, remappingFunction);
    }
}
