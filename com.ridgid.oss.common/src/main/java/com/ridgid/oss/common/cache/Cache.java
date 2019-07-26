package com.ridgid.oss.common.cache;

import java.util.Map;
import java.util.function.*;

@SuppressWarnings("unused")
public interface Cache<K, V> {
    void forceCleanup();

    int size();

    boolean isEmpty();

    V put(K key, V value);

    void putAll(Map<? extends K, ? extends V> m);

    void clear();

    V putIfAbsent(K key, V value);

    V remove(K key);

    boolean replace(K key, V oldValue, V newValue);

    V replace(K key, V value);

    V getOrDefault(K key, V defaultValue);

    void forEach(BiConsumer<? super K, ? super V> action);

    V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);

    V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);

    V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);

    V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction);

    void forEach(long parallelismThreshold, BiConsumer<? super K, ? super V> action);

    <U> void forEach(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> transformer, Consumer<? super U> action);

    <U> U search(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> searchFunction);

    <U> U reduce(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> transformer, BiFunction<? super U, ? super U, ? extends U> reducer);

    double reduceToDouble(long parallelismThreshold, ToDoubleBiFunction<? super K, ? super V> transformer, double basis, DoubleBinaryOperator reducer);

    long reduceToLong(long parallelismThreshold, ToLongBiFunction<? super K, ? super V> transformer, long basis, LongBinaryOperator reducer);

    int reduceToInt(long parallelismThreshold, ToIntBiFunction<? super K, ? super V> transformer, int basis, IntBinaryOperator reducer);

    void forEachKey(long parallelismThreshold, Consumer<? super K> action);

    <U> void forEachKey(long parallelismThreshold, Function<? super K, ? extends U> transformer, Consumer<? super U> action);

    <U> U searchKeys(long parallelismThreshold, Function<? super K, ? extends U> searchFunction);
}
