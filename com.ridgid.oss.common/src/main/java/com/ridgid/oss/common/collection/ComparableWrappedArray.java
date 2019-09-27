package com.ridgid.oss.common.collection;

import com.ridgid.oss.common.helper.ComparisonHelpers;

import java.util.Arrays;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public class ComparableWrappedArray<T extends Comparable<T>>
    implements Comparable<ComparableWrappedArray<T>>
{
    private final T[] array;

    public ComparableWrappedArray(T[] array) {
        this.array = array;
    }

    public Stream<T> unwrap() {
        return Arrays.stream(array);
    }

    @Override
    public int compareTo(ComparableWrappedArray<T> o) {
        return ComparisonHelpers.comparingNullsLast(unwrap(), o.unwrap());
    }
}
