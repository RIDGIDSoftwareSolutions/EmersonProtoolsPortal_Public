package com.ridgid.oss.cache.redis;

import com.ridgid.oss.common.cache.Cache;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class RedisUnmanagedCache<K, V> implements Cache<K, V> {
    private final RedissonClient client;
    private final String cacheName;

    public RedisUnmanagedCache(RedissonClient client,
                               String cacheName) {
        this.client = client;
        this.cacheName = cacheName;
    }

    @Override
    public int size() {
        return getCache().size();
    }

    RMap<K, V> getCache() {
        return client.getMap(cacheName);
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
