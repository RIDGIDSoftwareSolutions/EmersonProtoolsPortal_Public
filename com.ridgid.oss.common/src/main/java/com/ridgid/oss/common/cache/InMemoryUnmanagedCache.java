package com.ridgid.oss.common.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal", "unused", "SpellCheckingInspection"})
public class InMemoryUnmanagedCache<K, V> implements Cache<K, V> {

    protected final ConcurrentHashMap<K, V> cache;

    public InMemoryUnmanagedCache(short initialCapacity) {
        this.cache = new ConcurrentHashMap<>(initialCapacity);
    }

    @Override
    public void clear() {
        cache.clear();
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
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        cache.forEach(action);
    }

    @Override
    public Stream<Map.Entry<K, V>> stream() {
        return cache.entrySet().stream();
    }

    @Override
    public Stream<K> streamKeys() {
        return cache.keySet().stream();
    }

    @Override
    public Stream<V> streamValues() {
        return cache.values().stream();
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        cache.replaceAll(function);
    }

    @Override
    public V put(K key, V value) {
        return cache.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        cache.putAll(m);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return cache.putIfAbsent(key, value);
    }

    @Override
    public V remove(K key) {
        return cache.remove(key);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return cache.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return cache.replace(key, value);
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        return cache.getOrDefault(key, defaultValue);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return cache.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return cache.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return cache.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return cache.merge(key, value, remappingFunction);
    }
}
