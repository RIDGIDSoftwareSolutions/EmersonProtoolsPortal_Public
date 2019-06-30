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


    /**
     * @param objects
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public static int comparing(Comparable... objects) {
        if (objects.length % 2 != 0 || objects.length < 2)
            throw new IllegalArgumentException("Number of Objects to Compare must be even and there must be at least 2 objects to compare");
        int rv = 0;
        for (int i = 0; i < objects.length; i += 2) {
            rv = objects[i].compareTo(objects[i + 1]);
            if (rv != 0) return rv;
        }
        return rv;
    }

    /**
     * @param objects
     * @return
     */
    public static int comparingNullsFirst(Comparable... objects) {
        return comparingWithNulls(-1, 1, objects);
    }

    /**
     * @param objects
     * @return
     */
    public static int comparingNullsLast(Comparable... objects) {
        return comparingWithNulls(1, -1, objects);
    }

    @SuppressWarnings({"unchecked"})
    private static int comparingWithNulls(int firstOnlyNullCompareValue, int lastOnlyNullCompareValue, Comparable[] objects) {
        if (objects.length % 2 != 0 || objects.length < 2)
            throw new IllegalArgumentException("Number of Objects to Compare must be even and there must be at least 2 objects to compare");
        int rv = 0;
        for (int i = 0; i < objects.length; i += 2) {
            rv = objects[i] == null
                    ?
                    (
                            objects[i + 1] == null
                                    ? 0
                                    : firstOnlyNullCompareValue
                    )
                    :
                    (
                            objects[i + 1] == null
                                    ? lastOnlyNullCompareValue
                                    : objects[i].compareTo(objects[i + 1])
                    );
            if (rv != 0) return rv;
        }
        return rv;
    }

}