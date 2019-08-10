package com.ridgid.oss.common.cache;

@SuppressWarnings({"unused", "SpellCheckingInspection", "WeakerAccess"})
public interface ExpirableCache<K, V extends Expirable> extends ManagedCache<K, V> {
}
