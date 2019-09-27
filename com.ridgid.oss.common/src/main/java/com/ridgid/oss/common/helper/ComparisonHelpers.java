package com.ridgid.oss.common.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;

/**
 *
 */
@SuppressWarnings({"unused", "JavaDoc", "WeakerAccess"})
public final class ComparisonHelpers
{

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
        if ( item.compareTo(rangeStart) < 0 ) return false;
        return item.compareTo(rangeEnd) < 0;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<? super T>> int comparing(Iterator<T> it1, Iterator<T> it2) {
        return comparingWithComparator(naturalOrder(), it1, it2);
    }

    public static <T extends Comparable<? super T>> int comparingNullsFirst(Iterator<T> it1, Iterator<T> it2) {
        return comparingWithComparator(ComparisonHelpers::comparingWithNullsFirst, it1, it2);
    }

    public static <T extends Comparable<? super T>> int comparingNullsLast(Iterator<T> it1, Iterator<T> it2) {
        return comparingWithComparator(ComparisonHelpers::comparingWithNullsLast, it1, it2);
    }

    /**
     * @param objects
     * @return
     */
    @SuppressWarnings("unchecked")
    public static int comparing(Comparable... objects) {
        return comparingWithComparator(naturalOrder(), objects);
    }

    /**
     * @param objects
     * @return
     */
    public static int comparingNullsFirst(Comparable... objects) {
        return comparingWithComparator(ComparisonHelpers::comparingWithNullsFirst, objects);
    }

    /**
     * @param objects
     * @return
     */
    public static int comparingNullsLast(Comparable... objects) {
        return comparingWithComparator(ComparisonHelpers::comparingWithNullsLast, objects);
    }

    public static <T extends Comparable<? super T>> int comparing(T[] lhs,
                                                                  T[] rhs)
    {
        return comparing(Arrays.stream(lhs).iterator(),
                         Arrays.stream(rhs).iterator());
    }

    public static <T extends Comparable<? super T>> int comparingNullsFirst(T[] lhs,
                                                                            T[] rhs)
    {
        return comparingNullsFirst(Arrays.stream(lhs).iterator(),
                                   Arrays.stream(rhs).iterator());
    }

    public static <T extends Comparable<? super T>> int comparingNullsLast(T[] lhs,
                                                                           T[] rhs)
    {
        return comparingNullsLast(Arrays.stream(lhs).iterator(),
                                  Arrays.stream(rhs).iterator());
    }

    public static <T extends Comparable<? super T>> int comparing(Stream<T> lhs,
                                                                  Stream<T> rhs)
    {
        return comparing(lhs.iterator(),
                         rhs.iterator());
    }

    public static <T extends Comparable<? super T>> int comparingNullsFirst(Stream<T> lhs,
                                                                            Stream<T> rhs)
    {
        return comparingNullsFirst(lhs.iterator(),
                                   rhs.iterator());
    }

    public static <T extends Comparable<? super T>> int comparingNullsLast(Stream<T> lhs,
                                                                           Stream<T> rhs)
    {
        return comparingNullsLast(lhs.iterator(),
                                  rhs.iterator());
    }

    public static <T extends Comparable<? super T>> int comparing(Collection<T> lhs,
                                                                  Collection<T> rhs)
    {
        return comparing(lhs.iterator(),
                         rhs.iterator());
    }

    public static <T extends Comparable<? super T>> int comparingNullsFirst(Collection<T> lhs,
                                                                            Collection<T> rhs)
    {
        return comparingNullsFirst(lhs.iterator(),
                                   rhs.iterator());
    }

    public static <T extends Comparable<? super T>> int comparingNullsLast(Collection<T> lhs,
                                                                           Collection<T> rhs)
    {
        return comparingNullsLast(lhs.iterator(),
                                  rhs.iterator());
    }

    private static <T extends Comparable<? super T>> int comparingWithComparator(Comparator<Comparable> comparator,
                                                                                 Iterator<T> it1,
                                                                                 Iterator<T> it2)
    {
        while ( it1.hasNext() && it2.hasNext() ) {
            Comparable<?> i1    = it1.next();
            Comparable<?> i2    = it2.next();
            int           order = comparator.compare(i1, i2);
            if ( order != 0 ) return order;
        }

        return it1.hasNext()
               ? 1
               : it2.hasNext()
                 ? -1
                 : 0;
    }

    private static int comparingWithComparator(Comparator<Comparable> comparator, Comparable[] objects) {
        verifyNumberOfObjectsIsEvenAndThrowIfNot(objects);
        int rv = 0;
        for ( int i = 0; i < objects.length; i += 2 ) {
            rv = comparator.compare(objects[i], objects[i + 1]);
            if ( rv != 0 ) return rv;
        }
        return rv;
    }

    private static int comparingWithNullsFirst(Comparable a, Comparable b) {
        return comparingWithNulls(-1, a, b);
    }

    private static int comparingWithNullsLast(Comparable a, Comparable b) {
        return comparingWithNulls(1, a, b);
    }

    @SuppressWarnings("unchecked")
    private static int comparingWithNulls(int firstOnlyNullCompareValue,
                                          Comparable a,
                                          Comparable b)
    {
        return a == null
               ?
               (
                   b == null
                   ? 0
                   : firstOnlyNullCompareValue
               )
               :
               (
                   b == null
                   ? -firstOnlyNullCompareValue
                   : a.compareTo(b)
               );
    }

    private static void verifyNumberOfObjectsIsEvenAndThrowIfNot(Comparable[] objects) {
        if ( objects.length % 2 != 0 )
            throw new IllegalArgumentException(
                "Number of Objects to Compare must be even");
    }

}
