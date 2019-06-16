package com.ridgid.oss.orm.listener;

@SuppressWarnings("unused")
public interface CreateModifyTrackingListener {
    void onCreate(Object entity);

    void onUpdate(Object entity);
}
