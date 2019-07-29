package com.ridgid.oss.common.hierarchy;

import com.ridgid.oss.common.callback.BiHandler;

@SuppressWarnings({"unused", "WeakerAccess"})
@FunctionalInterface
public interface VisitHandler<PARENT_T, T> extends BiHandler<PARENT_T, T, VisitStatus> {
}
