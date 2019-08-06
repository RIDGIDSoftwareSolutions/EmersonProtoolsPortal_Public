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
    N include(Function<T, CHILD_T> selector,
              View<CHILD_T, CHILD_T_VIEW> view) {
        return include
                (
                        selector,
                        view,
                        null
                );
    }

    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N include(Function<T, CHILD_T> selector) {
        return include
                (
                        selector,
                        (Consumer<SingleNode<T, CHILD_T, NC>>) null
                );
    }

    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N include(Function<T, CHILD_T> selector,
              View<CHILD_T, CHILD_T_VIEW> view,
              Consumer<SingleNode<T, CHILD_T_VIEW, NC>> childrenSelector) {
        return include
                (
                        (T t) -> view.apply(selector.apply(t)),
                        childrenSelector
                );
    }

    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N include(Function<T, CHILD_T> selector,
              Consumer<SingleNode<T, CHILD_T, NC>> childrenSelector);


    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N includeStream(Function<T, Stream<CHILD_T>> selector,
                    View<CHILD_T, CHILD_T_VIEW> view) {
        return includeStream
                (
                        selector,
                        view,
                        null
                );
    }

    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N includeStream(Function<T, Stream<CHILD_T>> selector) {
        return includeStream
                (
                        selector,
                        (Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>, NC>>) null
                );
    }

    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N includeStream(Function<T, Stream<CHILD_T>> selector,
                    View<CHILD_T, CHILD_T_VIEW> view,
                    Consumer<MultiNode<T, CHILD_T_VIEW, Stream<CHILD_T_VIEW>, NC>> childrenSelector) {
        return includeStream
                (
                        (T t) -> selector.apply(t).map(view),
                        childrenSelector
                );
    }

    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N includeStream(Function<T, Stream<CHILD_T>> selector,
                    Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>, NC>> childrenSelector);


    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N includeCollection(Function<T, Iterable<CHILD_T>> selector,
                        View<CHILD_T, CHILD_T_VIEW> view) {
        return includeCollection
                (
                        selector,
                        view,
                        null
                );
    }

    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N includeCollection(Function<T, Iterable<CHILD_T>> selector) {
        return includeCollection
                (
                        selector,
                        (Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>, NC>>) null
                );
    }

    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N includeCollection(Function<T, Iterable<CHILD_T>> selector,
                        View<CHILD_T, CHILD_T_VIEW> view,
                        Consumer<MultiNode<T, CHILD_T_VIEW, Iterable<CHILD_T_VIEW>, NC>> childrenSelector) {
        return includeCollection
                (
                        (T t) -> adapt(selector.apply(t)),
                        childrenSelector
                );
    }

    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N includeCollection(Function<T, Iterable<CHILD_T>> selector,
                        Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>, NC>> childrenSelector);


    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N includeArray(Function<T, CHILD_T[]> selector,
                   View<CHILD_T, CHILD_T_VIEW> view) {
        return includeArray
                (
                        selector,
                        view,
                        null
                );
    }

    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N includeArray(Function<T, CHILD_T[]> selector) {
        return includeArray
                (
                        selector,
                        (Consumer<MultiNode<T, CHILD_T, CHILD_T[], NC>>) null
                );
    }

    default <CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    N includeArray(Function<T, CHILD_T[]> selector,
                   View<CHILD_T, CHILD_T_VIEW> view,
                   Consumer<MultiNode<T, CHILD_T_VIEW, CHILD_T_VIEW[], NC>> childrenSelector) {
        return includeArray
                (
                        selector::apply,
                        childrenSelector
                );
    }

    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N includeArray(Function<T, CHILD_T[]> selector,
                   Consumer<MultiNode<T, CHILD_T, CHILD_T[], NC>> childrenSelector);
}