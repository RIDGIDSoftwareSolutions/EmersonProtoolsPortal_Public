package com.ridgid.oss.common.cache;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public interface Expirable {
    boolean isExpired();

    default boolean isNotExpired() {
        return !isExpired();
    }
}
