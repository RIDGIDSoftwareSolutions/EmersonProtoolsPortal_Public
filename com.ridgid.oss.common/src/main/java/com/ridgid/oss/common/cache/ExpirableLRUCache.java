package com.ridgid.oss.common.cache;

@SuppressWarnings({"unused", "WeakerAccess", "SpellCheckingInspection"})
public interface ExpirableLRUCache<K, V extends Expirable> extends LRUCache<K, V> {
}
