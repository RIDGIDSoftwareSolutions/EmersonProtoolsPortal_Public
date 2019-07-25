package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface Node<T> {

    default <CHILD_T> Node<T> one(Function<T, CHILD_T> selector) {
        return one(selector, null);
    }

    <CHILD_T> Node<T> one(Function<T, CHILD_T> selector,
                          Consumer<Node<CHILD_T>> childrenSelector);

    default <CHILD_T> Node<T> many(Function<T, Stream<CHILD_T>> selector) {
        return many(selector, null);
    }

    <CHILD_T> Node<T> many(Function<T, Stream<CHILD_T>> selector,
                           Consumer<Node<CHILD_T>> childrenSelector);

    default <CHILD_T> Node<T> many_i(Function<T, Iterable<CHILD_T>> selector) {
        return many_i(selector, null);
    }

    <CHILD_T> Node<T> many_i(Function<T, Iterable<CHILD_T>> selector,
                             Consumer<Node<CHILD_T>> childrenSelector);

    default <CHILD_T> Node<T> many_a(Function<T, CHILD_T[]> selector) {
        return many_a(selector, null);
    }

    <CHILD_T> Node<T> many_a(Function<T, CHILD_T[]> selector,
                             Consumer<Node<CHILD_T>> childrenSelector);

}