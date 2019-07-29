package com.ridgid.oss.common.callback;

@SuppressWarnings("unused")
@FunctionalInterface()
public interface BiHandler<T1, T2, R> {
    R handle(T1 t1, T2 t2);
}
