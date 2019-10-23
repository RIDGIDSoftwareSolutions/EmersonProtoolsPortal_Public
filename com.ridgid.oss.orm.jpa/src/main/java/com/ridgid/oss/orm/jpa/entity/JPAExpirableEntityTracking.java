package com.ridgid.oss.orm.jpa.entity;

import com.ridgid.oss.orm.entity.ExpirableEntityTracking;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.time.Duration;

@SuppressWarnings({"unused", "JavaDoc"})
@Embeddable
public class JPAExpirableEntityTracking
    implements
    ExpirableEntityTracking
{
    @SuppressWarnings("FieldHasSetterButNoGetter")
    @Transient
    private long loadTimeMillis;

    @Transient
    private final long timeUntilExpirationMillis;

    public JPAExpirableEntityTracking(Duration timeUntilExpiration) {
        timeUntilExpirationMillis = timeUntilExpiration.toMillis();
    }

    @Override
    public void setLoadTimeMillis(long loadTimeMillis) {
        this.loadTimeMillis = loadTimeMillis;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > (loadTimeMillis + timeUntilExpirationMillis);
    }

    @Override
    public String toString() {
        return "JPAExpirableEntityTracking{" +
               "loadTimeMillis=" + loadTimeMillis +
               ", timeUntilExpirationMillis=" + timeUntilExpirationMillis +
               '}';
    }
}
