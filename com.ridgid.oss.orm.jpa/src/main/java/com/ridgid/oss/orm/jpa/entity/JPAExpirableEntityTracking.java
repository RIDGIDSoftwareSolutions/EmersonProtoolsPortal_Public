package com.ridgid.oss.orm.jpa.entity;

import com.ridgid.oss.orm.entity.ExpirableEntityTracking;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.time.Duration;

@Embeddable
public class JPAExpirableEntityTracking implements ExpirableEntityTracking {
    @Transient
    private long loadTimeMillis;
    @Transient
    private long timeUntilExpirationMillis;

    public JPAExpirableEntityTracking(Duration timeUntilExpiration) {
        this.timeUntilExpirationMillis = timeUntilExpiration.toMillis();
    }

    @Override
    public void setLoadTimeMillis(long loadTimeMillis) {
        this.loadTimeMillis = loadTimeMillis;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > (loadTimeMillis + timeUntilExpirationMillis);
    }
}
