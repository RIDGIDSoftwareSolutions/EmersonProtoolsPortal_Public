package com.ridgid.oss.orm.entity;

import com.ridgid.oss.common.cache.Expirable;

public interface ExpirableEntityTracking extends Expirable {
    void setLoadTimeMillis(long loadTimeMillis);

    boolean isExpired();
}
