package com.ridgid.oss.common.callback;

@SuppressWarnings("unused")
@FunctionalInterface()
public interface Handler<T, R> {
    R handle(T t);
}
