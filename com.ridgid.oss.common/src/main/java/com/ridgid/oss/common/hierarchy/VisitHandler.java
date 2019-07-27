package com.ridgid.oss.common.hierarchy;

import com.ridgid.oss.common.callback.Handler;

@SuppressWarnings({"unused", "WeakerAccess"})
@FunctionalInterface
public interface VisitHandler<T> extends Handler<T, VisitStatus> {
}
