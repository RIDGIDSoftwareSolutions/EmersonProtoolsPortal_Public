package com.ridgid.oss.common.function;

@SuppressWarnings("unused")
public final class Function {

    public Function() {
    }

    public static <T, CT> java.util.function.Function<T, CT> replaceWith(CT ct) {
        return t -> ct;
    }

}
