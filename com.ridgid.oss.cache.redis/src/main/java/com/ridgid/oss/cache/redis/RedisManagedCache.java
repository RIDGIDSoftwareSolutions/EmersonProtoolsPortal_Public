package com.ridgid.oss.cache.redis;

import com.ridgid.oss.common.cache.ManagedCache;
import org.redisson.api.RedissonClient;

import java.util.Timer;
import java.util.TimerTask;

public class RedisManagedCache<K, V> extends RedisUnmanagedCache<K, V> implements ManagedCache<K, V> {
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
        super(client, cacheName);
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
            super.getCache().entrySet().stream()
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
}
