package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface Node<PARENT_T, T> {

    default <CHILD_T> Node<PARENT_T, T> single(Function<T, CHILD_T> selector) {
        return single(selector, null);
    }

    <CHILD_T> Node<PARENT_T, T> single(Function<T, CHILD_T> selector,
                                       Consumer<SingleNode<T, CHILD_T>> childrenConfigurer);

    default <CHILD_T> Node<PARENT_T, T> many(Function<T, Stream<CHILD_T>> selector) {
        return many(selector, null);
    }

    <CHILD_T> Node<PARENT_T, T> many(Function<T, Stream<CHILD_T>> selector,
                                     Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>>> childrenSelector);

    default <CHILD_T> Node<PARENT_T, T> collection(Function<T, Iterable<CHILD_T>> selector) {
        return collection(selector, null);
    }

    <CHILD_T> Node<PARENT_T, T> collection(Function<T, Iterable<CHILD_T>> selector,
                                           Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>>> childrenSelector);

    default <CHILD_T> Node<PARENT_T, T> array(Function<T, CHILD_T[]> selector) {
        return array(selector, null);
    }

    <CHILD_T> Node<PARENT_T, T> array(Function<T, CHILD_T[]> selector,
                                      Consumer<MultiNode<T, CHILD_T, CHILD_T[]>> childrenSelector);
}