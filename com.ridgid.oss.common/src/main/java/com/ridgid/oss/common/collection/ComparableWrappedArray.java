package com.ridgid.oss.common.collection;

import com.ridgid.oss.common.helper.ComparisonHelpers;

import java.util.Arrays;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ComparableWrappedArray<T extends Comparable<T>>
    implements Comparable<ComparableWrappedArray<T>>
{
    private final T[] array;

    public ComparableWrappedArray(T[] array) {
        this.array = array;
    }

    public Stream<T> stream() {
        return Arrays.stream(array);
    }

    @Override
    public int compareTo(ComparableWrappedArray<T> o) {
        return ComparisonHelpers.comparingNullsLast(stream(), o.stream());
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        ComparableWrappedArray<?> that = (ComparableWrappedArray<?>) o;
        return Arrays.equals(array, that.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }
}
