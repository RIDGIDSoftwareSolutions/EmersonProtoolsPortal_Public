package com.ridgid.oss.common.cache;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal", "unused"})
public class InMemoryKVCache<K, V extends Expirable> implements Cache<K, V> {

    private final ConcurrentHashMap<K, V> cache;
    private final Timer cleanupTimer;
    private final short maxCapacity;
    private final short evictToCapacity;

    public InMemoryKVCache(short timeoutCheckIntervalSeconds,
                           short initialCapacity,
                           short maxCapacity,
                           short evictToCapacity) {
        this.cache = new ConcurrentHashMap<>(initialCapacity);
        this.maxCapacity = maxCapacity;
        this.evictToCapacity = evictToCapacity;
        this.cleanupTimer = makeCleanupTimer
                (
                        timeoutCheckIntervalSeconds
                );
    }

    public final void forceCleanup() {
        checkCapacity();
    }

    private void checkCapacity() {
        if (cache.size() > evictToCapacity)
            cleanupTimer.schedule
                    (
                            makeCleanupTask(),
                            10
                    );
    }

    private Timer makeCleanupTimer(short timeoutCheckIntervalSeconds) {
        Timer timer = new Timer(true);
        timer.schedule
                (
                        makeCleanupTask(),
                        timeoutCheckIntervalSeconds * 1000,
                        timeoutCheckIntervalSeconds * 1000
                );
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

    protected void cleanup() {
        synchronized (cleanupTimer) {
            cache.entrySet()
                    .stream()
                    .filter(this::normalEvictionApplies)
                    .forEach(e -> this.remove(e.getKey()));
            if (cache.size() > maxCapacity)
                overCapacityEvictionSelector(cache.size(), evictToCapacity, cache.entrySet().stream())
                        .filter(entry -> cache.size() > evictToCapacity)
                        .forEach(entry -> this.remove(entry.getKey()));
        }
    }

    protected boolean normalEvictionApplies(Map.Entry<K, V> entry) {
        return false;
    }

    protected Stream<Map.Entry<K, V>> overCapacityEvictionSelector(int currentEntryCount,
                                                                   int targetEntryCount,
                                                                   Stream<Map.Entry<K, V>> entries) {
        return entries;
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public V put(K key, V value) {
        checkCapacity();
        return cache.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        cache.putAll(m);
        checkCapacity();
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        checkCapacity();
        return cache.putIfAbsent(key, value);
    }

    @Override
    public V remove(K key) {
        return cache.remove(key);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        checkCapacity();
        return cache.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        checkCapacity();
        return cache.replace(key, value);
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        return cache.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        cache.forEach(action);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        checkCapacity();
        return cache.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        checkCapacity();
        return cache.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        checkCapacity();
        return cache.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        checkCapacity();
        return cache.merge(key, value, remappingFunction);
    }

    @Override
    public void forEach(long parallelismThreshold, BiConsumer<? super K, ? super V> action) {
        cache.forEach(parallelismThreshold, action);
    }

    @Override
    public <U> void forEach(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> transformer, Consumer<? super U> action) {
        cache.forEach(parallelismThreshold, transformer, action);
    }

    @Override
    public <U> U search(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> searchFunction) {
        return cache.search(parallelismThreshold, searchFunction);
    }

    @Override
    public <U> U reduce(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> transformer, BiFunction<? super U, ? super U, ? extends U> reducer) {
        return cache.reduce(parallelismThreshold, transformer, reducer);
    }

    @Override
    public double reduceToDouble(long parallelismThreshold, ToDoubleBiFunction<? super K, ? super V> transformer, double basis, DoubleBinaryOperator reducer) {
        return cache.reduceToDouble(parallelismThreshold, transformer, basis, reducer);
    }

    @Override
    public long reduceToLong(long parallelismThreshold, ToLongBiFunction<? super K, ? super V> transformer, long basis, LongBinaryOperator reducer) {
        return cache.reduceToLong(parallelismThreshold, transformer, basis, reducer);
    }

    @Override
    public int reduceToInt(long parallelismThreshold, ToIntBiFunction<? super K, ? super V> transformer, int basis, IntBinaryOperator reducer) {
        return cache.reduceToInt(parallelismThreshold, transformer, basis, reducer);
    }

    @Override
    public void forEachKey(long parallelismThreshold, Consumer<? super K> action) {
        cache.forEachKey(parallelismThreshold, action);
    }

    @Override
    public <U> void forEachKey(long parallelismThreshold, Function<? super K, ? extends U> transformer, Consumer<? super U> action) {
        cache.forEachKey(parallelismThreshold, transformer, action);
    }

    @Override
    public <U> U searchKeys(long parallelismThreshold, Function<? super K, ? extends U> searchFunction) {
        return cache.searchKeys(parallelismThreshold, searchFunction);
    }
}
