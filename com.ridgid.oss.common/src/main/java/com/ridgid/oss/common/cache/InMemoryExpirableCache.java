package com.ridgid.oss.common.cache;

import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal", "unused", "SpellCheckingInspection"})
public class InMemoryExpirableCache<K, V extends Expirable>
        extends InMemoryManagedCache<K, V>
        implements ExpirableCache<K, V> {


    public InMemoryExpirableCache(short timeoutCheckIntervalSeconds,
                                  short initialCapacity,
                                  short maxCapacity,
                                  short evictToCapacity
    ) {
        super(
                timeoutCheckIntervalSeconds,
                initialCapacity,
                maxCapacity,
                evictToCapacity
        );
    }

    @Override
    protected boolean normalEvictionApplies(Map.Entry<K, V> entry) {
        return entry.getValue().isExpired();
    }

    @Override
    protected Stream<Map.Entry<K, V>> overCapacityEvictionSelector(int currentEntryCount,
                                                                   int targetEntryCount,
                                                                   Stream<Map.Entry<K, V>> entries) {
        return entries;
    }

}
