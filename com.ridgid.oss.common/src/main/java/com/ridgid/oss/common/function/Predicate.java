package com.ridgid.oss.common.function;

import java.util.function.Function;

public final class Predicate {

    public Predicate() {
    }

    public static <T, FT> java.util.function.Predicate<T> whereEquals(Function<T, FT> valueSelector, FT compareToValue) {
        return t -> valueSelector.apply(t).equals(compareToValue);
    }
}
