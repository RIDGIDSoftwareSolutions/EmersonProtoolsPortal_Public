package com.ridgid.oss.orm.jpa.listener;

import com.ridgid.oss.orm.CreateModifyTracking;
import com.ridgid.oss.orm.listener.CreateModifyTrackingListener;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@SuppressWarnings("unused")
public class JPACreateModifyTrackingListener
        implements CreateModifyTrackingListener {

    @PrePersist
    @Override
    public void onCreate(Object entity) {
        if (entity instanceof CreateModifyTracking) {
            CreateModifyTracking e = (CreateModifyTracking) entity;
            e.setCreated(LocalDateTime.now());
            e.setCreatedBy("*AUTO*");
        }
    }

    @PreUpdate
    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof CreateModifyTracking) {
            CreateModifyTracking e = (CreateModifyTracking) entity;
            e.setModified(LocalDateTime.now());
            e.setModifiedBy("*AUTO*");
        }
    }
}
