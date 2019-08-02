package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface Node<PARENT_T, T> {

    default <CHILD_T, N extends Node<PARENT_T, T>> N single(Function<T, CHILD_T> selector) {
        return single(selector, null);
    }

    <CHILD_T, N extends Node<PARENT_T, T>> N single(Function<T, CHILD_T> selector,
                                                    Consumer<SingleNode<T, CHILD_T>> childrenSelector);

    default <CHILD_T, N extends Node<PARENT_T, T>> N many(Function<T, Stream<CHILD_T>> selector) {
        return many(selector, null);
    }

    <CHILD_T, N extends Node<PARENT_T, T>> N many(Function<T, Stream<CHILD_T>> selector,
                                                  Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>>> childrenSelector);

    default <CHILD_T, N extends Node<PARENT_T, T>> N collection(Function<T, Iterable<CHILD_T>> selector) {
        return collection(selector, null);
    }

    <CHILD_T, N extends Node<PARENT_T, T>> N collection(Function<T, Iterable<CHILD_T>> selector,
                                                        Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>>> childrenSelector);

    default <CHILD_T, N extends Node<PARENT_T, T>> N array(Function<T, CHILD_T[]> selector) {
        return array(selector, null);
    }

    <CHILD_T, N extends Node<PARENT_T, T>> N array(Function<T, CHILD_T[]> selector,
                                                   Consumer<MultiNode<T, CHILD_T, CHILD_T[]>> childrenSelector);
}