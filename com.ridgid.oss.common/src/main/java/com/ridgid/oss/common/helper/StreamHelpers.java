package com.ridgid.oss.common.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class StreamHelpers {
    private StreamHelpers() {
    }

    public static <T, R, C extends Collection<R>> Function<T, Stream<C>> group(int groupSize,
                                                                               Supplier<C> collectionSupplier,
                                                                               BiFunction<C, R, C> combiner,
                                                                               Function<T, R> mapping,
                                                                               R endOfStreamSentinel) {
        return new Function<T, Stream<C>>() {
            private C group = collectionSupplier.get();

            @Override
            public Stream<C> apply(T t) {
                R r = mapping.apply(t);
                combiner.apply(group, r);
                if (Objects.equals(r, endOfStreamSentinel) || group.size() >= groupSize) {
                    C rv = group;
                    group = collectionSupplier.get();
                    return Stream.of(rv);
                }
                return Stream.empty();
            }
        };
    }

    public static <T, R> Function<T, Stream<List<R>>> group(int groupSize,
                                                            Function<T, R> mapping,
                                                            R endOfStreamSentinel) {
        return group(groupSize,
                () -> new ArrayList<>(groupSize),
                (l, r) -> {
                    l.add(r);
                    return l;
                },
                mapping,
                endOfStreamSentinel);
    }

    public static <T> Function<T, Stream<List<T>>> group(int groupSize,
                                                         T endOfStreamSentinel) {
        return group(groupSize, T -> T, endOfStreamSentinel);
    }

    public static <T, R> Function<T, Stream<List<R>>> group(int groupSize,
                                                            Function<T, R> mapping) {
        return group(groupSize, mapping, null);
    }

    public static <T> Function<T, Stream<List<T>>> group(int groupSize) {
        return group(groupSize, T -> T, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Function<T, Stream<T>> distinctBy(Function<T, ?>... fieldSelectors) {
        ConcurrentMap keysSeen = new ConcurrentHashMap<>();
        return t -> {
            ConcurrentMap keyMap = keysSeen;
            for (int i = 0; i < fieldSelectors.length - 1; i++) {
                Function<T, ?> selector = fieldSelectors[i];
                keyMap = (ConcurrentMap) keyMap.computeIfAbsent
                        (
                                selector.apply(t),
                                k -> new ConcurrentHashMap()
                        );
            }
            boolean seen = (boolean) keyMap.compute
                    (
                            fieldSelectors[fieldSelectors.length - 1].apply(t),
                            (k, v) -> v != null
                    );
            return seen ? null : Stream.of(t);
        };
    }

}
