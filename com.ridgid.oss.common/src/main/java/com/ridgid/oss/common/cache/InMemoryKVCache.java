package com.ridgid.oss.common.cache;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal", "unused"})
public class InMemoryKVCache<K, V extends Expirable> extends ConcurrentHashMap<K, V> {

    private final Timer cleanupTimer;
    private final short maxCapacity;
    private final short evictToCapacity;
    private final Function<Stream<Map.Entry<K, V>>, Stream<Map.Entry<K, V>>> overCapacityEvictionSelector;

    public InMemoryKVCache(short timeoutCheckIntervalSeconds,
                           short initialCapacity,
                           short maxCapacity,
                           short evictToCapacity,
                           Function<Stream<Map.Entry<K, V>>, Stream<Map.Entry<K, V>>> overCapacityEvictionSelector) {
        super(initialCapacity);
        this.maxCapacity = maxCapacity;
        this.evictToCapacity = evictToCapacity;
        this.overCapacityEvictionSelector = overCapacityEvictionSelector;
        this.cleanupTimer = makeCleanupTimer
                (
                        timeoutCheckIntervalSeconds
                );
    }

    public final void forceCleanup() {
        if (this.size() > evictToCapacity)
            cleanup();
    }

    private Timer makeCleanupTimer(short timeoutCheckIntervalSeconds) {
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                cleanup();
            }
        };
        timer.schedule(task, timeoutCheckIntervalSeconds * 1000, timeoutCheckIntervalSeconds * 1000);
        return timer;
    }

    private void cleanup() {
        synchronized (cleanupTimer) {
            this.entrySet().removeIf(this::normalEvictionApplies);
            if (this.size() > maxCapacity)
                overCapacityEvictionSelector
                        .apply(this.entrySet().stream())
                        .filter(entry -> this.size() > evictToCapacity)
                        .forEach(entry -> this.remove(entry.getKey()));
        }
    }

    protected boolean normalEvictionApplies(Map.Entry<K, V> entry) {
        return false;
    }


}
