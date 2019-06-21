package com.ridgid.oss.common.helper;

/**
 *
 */
@SuppressWarnings({"unused", "JavaDoc"})
public final class ComparisonHelpers {

    private ComparisonHelpers() {
    }

    /**
     * @param item
     * @param rangeStart
     * @param rangeEnd
     * @param <T>
     * @return
     */
    public static <T extends Comparable<T>> boolean isBetween(T item, T rangeStart, T rangeEnd) {
        if (item.compareTo(rangeStart) < 0) return false;
        return item.compareTo(rangeEnd) < 0;
    }
}