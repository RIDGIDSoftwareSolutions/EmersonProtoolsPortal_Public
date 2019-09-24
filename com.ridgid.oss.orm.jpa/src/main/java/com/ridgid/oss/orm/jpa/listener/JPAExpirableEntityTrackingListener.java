package com.ridgid.oss.orm.jpa.listener;

import com.ridgid.oss.orm.entity.ExpirableEntityTracking;
import com.ridgid.oss.orm.listener.ExpirableEntityTrackingListener;

import javax.persistence.PostLoad;

public class JPAExpirableEntityTrackingListener
    implements ExpirableEntityTrackingListener
{
    @PostLoad
    @Override
    public void onLoad(Object entity) {
        if (entity instanceof ExpirableEntityTracking) {
            ExpirableEntityTracking e = (ExpirableEntityTracking) entity;
            e.setLoadTimeMillis(System.currentTimeMillis());
        }
    }
}
