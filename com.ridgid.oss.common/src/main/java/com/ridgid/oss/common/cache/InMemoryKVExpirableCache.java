package com.ridgid.oss.common.cache;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public final class InMemoryKVExpirableCache<K, V extends Expirable> extends InMemoryKVCache<K, V> {

    public InMemoryKVExpirableCache(short timeoutCheckIntervalSeconds,
                                    short initialCapacity,
                                    short maxCapacity,
                                    short evictToCapacity,
                                    Function<Stream<Map.Entry<K, V>>, Stream<Map.Entry<K, V>>> overCapacityEvictor) {
        super(timeoutCheckIntervalSeconds,
                initialCapacity,
                maxCapacity,
                evictToCapacity,
                overCapacityEvictor);
    }

    @Override
    protected boolean normalEvictionApplies(Map.Entry<K, V> entry) {
        return entry.getValue().isExpired();
    }
}
