package com.ridgid.oss.common.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public final class InMemoryKVExpirableLRUCache<K, V extends Expirable>
        extends InMemoryExpirableKVCache<K, V>
        implements ExpirableLRUCache<K, V> {

    private final ConcurrentHashMap<K, Long> lastUsed = new ConcurrentHashMap<>();
    private long removeBeforeTime;

    public InMemoryKVExpirableLRUCache(short timeoutCheckIntervalSeconds,
                                       short initialCapacity,
                                       short maxCapacity,
                                       short evictToCapacity) {
        super(timeoutCheckIntervalSeconds,
                initialCapacity,
                maxCapacity,
                evictToCapacity);

    }

    @Override
    protected boolean normalEvictionApplies(Map.Entry<K, V> entry) {
        return entry.getValue().isExpired();
    }

    @Override
    protected Stream<Map.Entry<K, V>> overCapacityEvictionSelector(int currentEntryCount,
                                                                   int targetEntryCount,
                                                                   Stream<Map.Entry<K, V>> entries) {
        removeBeforeTime = lastUsed
                .values()
                .stream()
                .mapToLong(l -> l)
                .sorted()
                .limit(currentEntryCount - targetEntryCount)
                .max()
                .orElse(System.currentTimeMillis());
        return entries.filter(this::isLastUsedBeforeCutoff);
    }

    private boolean isLastUsedBeforeCutoff(Map.Entry<K, V> e) {
        return lastUsed.computeIfAbsent
                (
                        e.getKey(),
                        k -> System.currentTimeMillis()
                ) <= removeBeforeTime;
    }

    private void updateLastUsed(K key) {
        lastUsed.compute(key, (k, lu) -> System.currentTimeMillis());
    }

    private void updateLastUsed(Map<? extends K, ? extends V> m) {
        m.forEach((k, v) -> updateLastUsed(k));
    }

    private void removeLastUsed(K key) {
        lastUsed.remove(key);
    }

    @Override
    public V put(K key, V value) {
        updateLastUsed(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        updateLastUsed(m);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        updateLastUsed(key);
        return super.putIfAbsent(key, value);
    }

    @Override
    public V remove(K key) {
        removeLastUsed(key);
        return super.remove(key);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        updateLastUsed(key);
        return super.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        updateLastUsed(key);
        return super.replace(key, value);
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        updateLastUsed(key);
        return super.getOrDefault(key, defaultValue);
    }

    @Override
    public boolean containsKey(K key) {
        updateLastUsed(key);
        return super.containsKey(key);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        super.replaceAll((k, v) -> {
            updateLastUsed(k);
            return function.apply(k, v);
        });
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        super.forEach((k, v) -> {
            updateLastUsed(k);
            action.accept(k, v);
        });
    }

    @Override
    public Stream<Map.Entry<K, V>> stream() {
        return super.stream().peek(e -> updateLastUsed(e.getKey()));
    }

    @Override
    public Stream<K> streamKeys() {
        return super.streamKeys().peek(this::updateLastUsed);
    }

    @Override
    public Stream<V> streamValues() {
        return super.streamValues();
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        updateLastUsed(key);
        return super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        updateLastUsed(key);
        return super.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        updateLastUsed(key);
        return super.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        updateLastUsed(key);
        return super.merge(key, value, remappingFunction);
    }
}
