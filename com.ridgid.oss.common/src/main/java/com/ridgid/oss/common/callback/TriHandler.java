package com.ridgid.oss.common.callback;

@SuppressWarnings("unused")
@FunctionalInterface()
public interface TriHandler<T1, T2, T3, R> {
    R handle(T1 t1, T2 t2, T3 t3);
}
