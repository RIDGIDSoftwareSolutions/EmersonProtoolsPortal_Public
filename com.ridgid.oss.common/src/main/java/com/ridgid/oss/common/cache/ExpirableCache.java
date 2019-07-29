package com.ridgid.oss.common.cache;

@SuppressWarnings("unused")
public interface ExpirableCache<K, V extends Expirable> extends Cache<K, V> {
}
