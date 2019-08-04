package com.ridgid.oss.common.helper;

import java.util.Iterator;
import java.util.function.Consumer;

public final class IterableHelper {

    private IterableHelper() {
    }

    public static <T extends R, R> Iterable<R> adapt(Iterable<T> source) {
        return () -> new Iterator<R>() {
            Iterator<T> it = source.iterator();

            @Override
            public void remove() {
                it.remove();
            }

            public void forEachRemaining(Consumer<? super R> action) {
                it.forEachRemaining(action);
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public R next() {
                return it.next();
            }
        };
    }
}
