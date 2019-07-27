package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface Node<T> {

    default <CHILD_T> Node<T> single(Function<T, CHILD_T> selector) {
        return single(selector, null);
    }

    <CHILD_T> Node<T> single(Function<T, CHILD_T> selector,
                             Consumer<Node<CHILD_T>> childrenSelector);

    default <CHILD_T> Node<T> many(Function<T, Stream<CHILD_T>> selector) {
        return many(selector, null);
    }

    <CHILD_T> Node<T> many(Function<T, Stream<CHILD_T>> selector,
                           Consumer<Node<CHILD_T>> childrenSelector);

    default <CHILD_T> Node<T> collection(Function<T, Iterable<CHILD_T>> selector) {
        return collection(selector, null);
    }

    <CHILD_T> Node<T> collection(Function<T, Iterable<CHILD_T>> selector,
                                 Consumer<Node<CHILD_T>> childrenSelector);

    default <CHILD_T> Node<T> array(Function<T, CHILD_T[]> selector) {
        return array(selector, null);
    }

    <CHILD_T> Node<T> array(Function<T, CHILD_T[]> selector,
                            Consumer<Node<CHILD_T>> childrenSelector);

    void whenVisited(Consumer<NodeVisitorConfiguration<T>> visitConfigurer);
}