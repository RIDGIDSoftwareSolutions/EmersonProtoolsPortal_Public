package com.ridgid.oss.common.helper;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

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


    /**
     * @param objects
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public static int comparing(Comparable... objects) {
        if ( objects.length % 2 != 0 || objects.length < 2 )
            throw new IllegalArgumentException(
                "Number of Objects to Compare must be even and there must be at least 2 objects to compare");
        int rv = 0;
        for ( int i = 0; i < objects.length; i += 2 ) {
            rv = objects[i].compareTo(objects[i + 1]);
            if ( rv != 0 ) return rv;
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
    private static int comparingWithNulls(int firstOnlyNullCompareValue,
                                          int lastOnlyNullCompareValue,
                                          Comparable[] objects)
    {
        if ( objects.length % 2 != 0 || objects.length < 2 )
            throw new IllegalArgumentException(
                "Number of Objects to Compare must be even and there must be at least 2 objects to compare");
        int rv = 0;
        for ( int i = 0; i < objects.length; i += 2 ) {
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
            if ( rv != 0 ) return rv;
        }
        return rv;
    }

    public static int comparing(Stream<Comparable<?>> s1,
                                Stream<Comparable<?>> s2)
    {
        Iterator<Comparable<?>> it1 = s1.iterator();
        Iterator<Comparable<?>> it2 = s2.iterator();

        while ( it1.hasNext() && it2.hasNext() ) {
            Comparable<?> i1    = it1.next();
            Comparable<?> i2    = it2.next();
            int           order = ComparisonHelpers.comparing(i1, i2);
            if ( order != 0 ) return order;
        }

        return it1.hasNext()
               ? 1
               : it2.hasNext()
                 ? -1
                 : 0;
    }

    public static int comparingNullsFirst(Stream<Comparable<?>> s1,
                                          Stream<Comparable<?>> s2)
    {
        Iterator<Comparable<?>> it1 = s1.iterator();
        Iterator<Comparable<?>> it2 = s2.iterator();

        while ( it1.hasNext() && it2.hasNext() ) {
            Comparable<?> i1    = it1.next();
            Comparable<?> i2    = it2.next();
            int           order = ComparisonHelpers.comparingNullsFirst(i1, i2);
            if ( order != 0 ) return order;
        }

        return it1.hasNext()
               ? 1
               : it2.hasNext()
                 ? -1
                 : 0;
    }

    public static int comparingNullsLast(Stream<Comparable<?>> s1,
                                         Stream<Comparable<?>> s2)
    {
        Iterator<Comparable<?>> it1 = s1.iterator();
        Iterator<Comparable<?>> it2 = s2.iterator();

        while ( it1.hasNext() && it2.hasNext() ) {
            Comparable<?> i1    = it1.next();
            Comparable<?> i2    = it2.next();
            int           order = ComparisonHelpers.comparingNullsLast(i1, i2);
            if ( order != 0 ) return order;
        }

        return it1.hasNext()
               ? 1
               : it2.hasNext()
                 ? -1
                 : 0;
    }

    public static <T extends Comparable<? super T>> int comparingNullsLast(List<T> lhs, List<T> rhs) {
        return comparingNullsLast(lhs.stream().map(identity()),
                                  rhs.stream().map(identity()));
    }
}
