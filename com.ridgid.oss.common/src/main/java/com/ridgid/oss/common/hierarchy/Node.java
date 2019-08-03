package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface Node<PARENT_T, T> {

    default <CHILD_T, N extends Node<PARENT_T, T>> N use(Function<T, CHILD_T> selector) {
        return use(selector, null);
    }

    <CHILD_T, N extends Node<PARENT_T, T>> N use(Function<T, CHILD_T> selector,
                                                 Consumer<SingleNode<T, CHILD_T>> childrenSelector);

    default <CHILD_T, N extends Node<PARENT_T, T>> N selectAll(Function<T, Stream<CHILD_T>> selector) {
        return selectAll(selector, null);
    }

    <CHILD_T, N extends Node<PARENT_T, T>> N selectAll(Function<T, Stream<CHILD_T>> selector,
                                                       Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>>> childrenSelector);

    default <CHILD_T, N extends Node<PARENT_T, T>> N consumeAll(Function<T, Iterable<CHILD_T>> selector) {
        return consumeAll(selector, null);
    }

    <CHILD_T, N extends Node<PARENT_T, T>> N consumeAll(Function<T, Iterable<CHILD_T>> selector,
                                                        Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>>> childrenSelector);

    default <CHILD_T, N extends Node<PARENT_T, T>> N accessAll(Function<T, CHILD_T[]> selector) {
        return accessAll(selector, null);
    }

    <CHILD_T, N extends Node<PARENT_T, T>> N accessAll(Function<T, CHILD_T[]> selector,
                                                       Consumer<MultiNode<T, CHILD_T, CHILD_T[]>> childrenSelector);
}