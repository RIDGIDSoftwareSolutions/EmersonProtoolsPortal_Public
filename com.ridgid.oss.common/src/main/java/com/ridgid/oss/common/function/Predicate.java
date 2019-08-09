package com.ridgid.oss.common.function;

import java.util.function.Function;

public final class Predicate {

    public Predicate() {
    }

    public static <T> java.util.function.Predicate<T> whereEquals(T compareToValue) {
        return t -> t.equals(compareToValue);
    }

    public static <T extends Comparable<T>> java.util.function.Predicate<T> whereLessThan(T compareToValue) {
        return t -> t.compareTo(compareToValue) < 0;
    }

    public static <T extends Comparable<T>> java.util.function.Predicate<T> whereLessThanOrEqualTo(T compareToValue) {
        return t -> t.compareTo(compareToValue) <= 0;
    }

    public static <T extends Comparable<T>> java.util.function.Predicate<T> whereGreaterThan(T compareToValue) {
        return t -> t.compareTo(compareToValue) > 0;
    }

    public static <T extends Comparable<T>> java.util.function.Predicate<T> whereGreaterThanOrEqualTo(T compareToValue) {
        return t -> t.compareTo(compareToValue) >= 0;
    }


    public static <T, FT> java.util.function.Predicate<T> wherePropertyEquals(Function<T, FT> valueSelector, FT compareToValue) {
        return t -> valueSelector.apply(t).equals(compareToValue);
    }

    public static <T, FT extends Comparable<FT>> java.util.function.Predicate<T> wherePropertyLessThan(Function<T, FT> valueSelector, FT compareToValue) {
        return t -> valueSelector.apply(t).compareTo(compareToValue) < 0;
    }

    public static <T, FT extends Comparable<FT>> java.util.function.Predicate<T> wherePropertyLessThanOrEqualTo(Function<T, FT> valueSelector, FT compareToValue) {
        return t -> valueSelector.apply(t).compareTo(compareToValue) <= 0;
    }

    public static <T, FT extends Comparable<FT>> java.util.function.Predicate<T> wherePropertyGreaterThan(Function<T, FT> valueSelector, FT compareToValue) {
        return t -> valueSelector.apply(t).compareTo(compareToValue) > 0;
    }

    public static <T, FT extends Comparable<FT>> java.util.function.Predicate<T> wherePropertyGreaterThanOrEqualTo(Function<T, FT> valueSelector, FT compareToValue) {
        return t -> valueSelector.apply(t).compareTo(compareToValue) >= 0;
    }

}
