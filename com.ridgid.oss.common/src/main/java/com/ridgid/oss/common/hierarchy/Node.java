package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface Node<T> {

    default <CHILD_T> Node<T> hasOne(Function<T, CHILD_T> selector) {
        return hasOne(selector, null);
    }

    <CHILD_T> Node<T> hasOne(Function<T, CHILD_T> selector,
                             Consumer<Node<CHILD_T>> childrenSelector);

    default <CHILD_T> Node<T> hasMany(Function<T, Stream<CHILD_T>> selector) {
        return hasMany(selector, null);
    }

    <CHILD_T> Node<T> hasMany(Function<T, Stream<CHILD_T>> selector,
                              Consumer<Node<CHILD_T>> childrenSelector);

    default <CHILD_T> Node<T> hasMany_i(Function<T, Iterable<CHILD_T>> selector) {
        return hasMany_i(selector, null);
    }

    <CHILD_T> Node<T> hasMany_i(Function<T, Iterable<CHILD_T>> selector,
                                Consumer<Node<CHILD_T>> childrenSelector);

    default <CHILD_T> Node<T> hasMany_a(Function<T, CHILD_T[]> selector) {
        return hasMany_a(selector, null);
    }

    <CHILD_T> Node<T> hasMany_a(Function<T, CHILD_T[]> selector,
                                Consumer<Node<CHILD_T>> childrenSelector);

}