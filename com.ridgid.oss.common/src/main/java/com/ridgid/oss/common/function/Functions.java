package com.ridgid.oss.common.function;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class Functions
{
    public Functions() {
    }

    public static <T, CT> Function<T, CT> replaceWith(CT ct) {
        return t -> ct;
    }

}
