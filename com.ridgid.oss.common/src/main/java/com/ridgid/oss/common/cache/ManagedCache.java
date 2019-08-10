package com.ridgid.oss.common.cache;

@SuppressWarnings("unused")
public interface ManagedCache<K, V> extends Cache<K, V> {
    void forceCleanup();
}
