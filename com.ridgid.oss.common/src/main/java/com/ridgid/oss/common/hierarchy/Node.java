package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.ridgid.oss.common.helper.IterableHelper.adapt;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface Node<PARENT_T, T> {

    final class View<T extends T_VIEW, T_VIEW>
            implements Function<T, T_VIEW> {
        private View(Class<T_VIEW> as) {
        }

        @Override
        public T_VIEW apply(T t) {
            return t;
        }
    }

    static <T_VIEW, T extends T_VIEW>
    View<T, T_VIEW> viewAs(Class<T_VIEW> as) {
        return new View<>(as);
    }

    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N with(Function<T, CHILD_T> selector,
           View<CHILD_T, CHILD_T_VIEW> view) {
        return with
                (
                        selector,
                        view,
                        null
                );
    }

    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N with(Function<T, CHILD_T> selector) {
        return with
                (
                        selector,
                        (Consumer<SingleNode<T, CHILD_T, NC>>) null
                );
    }

    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N with(Function<T, CHILD_T> selector,
           View<CHILD_T, CHILD_T_VIEW> view,
           Consumer<SingleNode<T, CHILD_T_VIEW, NC>> childrenSelector) {
        return with
                (
                        (T t) -> view.apply(selector.apply(t)),
                        childrenSelector
                );
    }

    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N with(Function<T, CHILD_T> selector,
           Consumer<SingleNode<T, CHILD_T, NC>> childrenSelector);


    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N selectAll(Function<T, Stream<CHILD_T>> selector,
                View<CHILD_T, CHILD_T_VIEW> view) {
        return selectAll
                (
                        selector,
                        view,
                        null
                );
    }

    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N selectAll(Function<T, Stream<CHILD_T>> selector) {
        return selectAll
                (
                        selector,
                        (Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>, NC>>) null
                );
    }

    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N selectAll(Function<T, Stream<CHILD_T>> selector,
                View<CHILD_T, CHILD_T_VIEW> view,
                Consumer<MultiNode<T, CHILD_T_VIEW, Stream<CHILD_T_VIEW>, NC>> childrenSelector) {
        return selectAll
                (
                        (T t) -> selector.apply(t).map(view),
                        childrenSelector
                );
    }

    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N selectAll(Function<T, Stream<CHILD_T>> selector,
                Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>, NC>> childrenSelector);


    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N consumeAll(Function<T, Iterable<CHILD_T>> selector,
                 View<CHILD_T, CHILD_T_VIEW> view) {
        return consumeAll
                (
                        selector,
                        view,
                        null
                );
    }

    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N consumeAll(Function<T, Iterable<CHILD_T>> selector) {
        return consumeAll
                (
                        selector,
                        (Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>, NC>>) null
                );
    }

    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N consumeAll(Function<T, Iterable<CHILD_T>> selector,
                 View<CHILD_T, CHILD_T_VIEW> view,
                 Consumer<MultiNode<T, CHILD_T_VIEW, Iterable<CHILD_T_VIEW>, NC>> childrenSelector) {
        return consumeAll
                (
                        (T t) -> adapt(selector.apply(t)),
                        childrenSelector
                );
    }

    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N consumeAll(Function<T, Iterable<CHILD_T>> selector,
                 Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>, NC>> childrenSelector);


    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N accessAll(Function<T, CHILD_T[]> selector,
                View<CHILD_T, CHILD_T_VIEW> view) {
        return accessAll
                (
                        selector,
                        view,
                        null
                );
    }

    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N accessAll(Function<T, CHILD_T[]> selector) {
        return accessAll
                (
                        selector,
                        (Consumer<MultiNode<T, CHILD_T, CHILD_T[], NC>>) null
                );
    }

    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N accessAll(Function<T, CHILD_T[]> selector,
                View<CHILD_T, CHILD_T_VIEW> view,
                Consumer<MultiNode<T, CHILD_T_VIEW, CHILD_T_VIEW[], NC>> childrenSelector) {
        return accessAll
                (
                        selector::apply,
                        childrenSelector
                );
    }

    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N accessAll(Function<T, CHILD_T[]> selector,
                Consumer<MultiNode<T, CHILD_T, CHILD_T[], NC>> childrenSelector);
}