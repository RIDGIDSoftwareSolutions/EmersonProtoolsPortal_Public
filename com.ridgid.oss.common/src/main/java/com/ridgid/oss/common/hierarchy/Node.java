package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.ridgid.oss.common.helper.IterableHelper.adapt;

@SuppressWarnings({"unused", "UnusedReturnValue", "DeprecatedIsStillUsed"})
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

    final class SingularChild<PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>> {

        final Function<T, CHILD_T> selector;
        final Consumer<SingleNode<T, CHILD_T, NC>> childrenSelector;

        SingularChild(
                Function<T, CHILD_T> selector
        ) {
            this.selector = selector;
            this.childrenSelector = null;
        }

        <FULL_CHILD_T extends CHILD_T>
        SingularChild(
                Function<T, FULL_CHILD_T> selector,
                View<FULL_CHILD_T, CHILD_T> view
        ) {
            this.selector = t -> view.apply(selector.apply(t));
            this.childrenSelector = null;
        }

        SingularChild(
                Function<T, CHILD_T> selector,
                Consumer<SingleNode<T, CHILD_T, NC>> childrenSelector
        ) {
            this.selector = selector;
            this.childrenSelector = childrenSelector;
        }

        <FULL_CHILD_T extends CHILD_T>
        SingularChild(
                Function<T, FULL_CHILD_T> selector,
                View<FULL_CHILD_T, CHILD_T> view,
                Consumer<SingleNode<T, CHILD_T, NC>> childrenSelector
        ) {
            this.selector = t -> view.apply(selector.apply(t));
            this.childrenSelector = childrenSelector;
        }

        void takeThe(Node<PARENT_T, T> node) {
            if (childrenSelector == null)
                node.with(selector);
            else
                node.with(selector, childrenSelector);
        }
    }

    static <PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>>
    SingularChild<PARENT_T, T, CHILD_T, NC> individual(Function<T, CHILD_T> selector) {
        return new SingularChild<>
                (
                        selector
                );
    }

    static <PARENT_T, T, CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    SingularChild<PARENT_T, T, CHILD_T_VIEW, NC> individual(Function<T, CHILD_T> selector,
                                                            View<CHILD_T, CHILD_T_VIEW> view) {
        return new SingularChild<>
                (
                        selector,
                        view
                );
    }

    static <PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>>
    SingularChild<PARENT_T, T, CHILD_T, NC> individual(Function<T, CHILD_T> selector,
                                                       Consumer<SingleNode<T, CHILD_T, NC>> childrenSelector) {
        return new SingularChild<>
                (
                        selector,
                        childrenSelector
                );
    }

    static <PARENT_T, T, CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    SingularChild<PARENT_T, T, CHILD_T_VIEW, NC> individual(Function<T, CHILD_T> selector,
                                                            View<CHILD_T, CHILD_T_VIEW> view,
                                                            Consumer<SingleNode<T, CHILD_T_VIEW, NC>> childrenSelector) {
        return new SingularChild<>
                (
                        selector,
                        view,
                        childrenSelector
                );
    }

    abstract class MultiChild<PARENT_T, T, CHILD_T, CT, NC extends Consumer<Node<T, CHILD_T>>> {

        final Function<T, CT> selector;
        final Consumer<MultiNode<T, CHILD_T, CT, NC>> childrenSelector;

        MultiChild(
                Function<T, CT> selector
        ) {
            this.selector = selector;
            this.childrenSelector = null;
        }

        MultiChild(
                Function<T, CT> selector,
                Consumer<MultiNode<T, CHILD_T, CT, NC>> childrenSelector
        ) {
            this.selector = selector;
            this.childrenSelector = childrenSelector;
        }

        abstract void takeThese(Node<PARENT_T, T> node);
    }

    final class ChildStreamOf<PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>>
            extends MultiChild<PARENT_T, T, CHILD_T, Stream<CHILD_T>, NC> {

        ChildStreamOf(
                Function<T, Stream<CHILD_T>> selector
        ) {
            super(selector);
        }

        ChildStreamOf(
                Function<T, Stream<CHILD_T>> selector,
                Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>, NC>> childrenSelector
        ) {
            super(selector, childrenSelector);
        }

        <FULL_CHILD_T extends CHILD_T>
        ChildStreamOf(
                Function<T, Stream<FULL_CHILD_T>> selector,
                View<FULL_CHILD_T, CHILD_T> view
        ) {
            super(t -> selector.apply(t).map(view));
        }

        <FULL_CHILD_T extends CHILD_T>
        ChildStreamOf(
                Function<T, Stream<FULL_CHILD_T>> selector,
                View<FULL_CHILD_T, CHILD_T> view,
                Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>, NC>> childrenSelector
        ) {
            super(t -> selector.apply(t).map(view), childrenSelector);
        }

        void takeThese(Node<PARENT_T, T> node) {
            if (childrenSelector == null)
                node.selectAll(selector);
            else
                node.selectAll(selector, childrenSelector);
        }
    }

    final class ChildIterableOf<PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>>
            extends MultiChild<PARENT_T, T, CHILD_T, Iterable<CHILD_T>, NC> {

        ChildIterableOf(
                Function<T, Iterable<CHILD_T>> selector
        ) {
            super(selector);
        }

        ChildIterableOf(
                Function<T, Iterable<CHILD_T>> selector,
                Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>, NC>> childrenSelector
        ) {
            super(selector, childrenSelector);
        }

        <FULL_CHILD_T extends CHILD_T>
        ChildIterableOf(
                Function<T, Iterable<FULL_CHILD_T>> selector,
                View<FULL_CHILD_T, CHILD_T> view
        ) {
            super(t -> adapt(selector.apply(t)));
        }

        <FULL_CHILD_T extends CHILD_T>
        ChildIterableOf(
                Function<T, Iterable<FULL_CHILD_T>> selector,
                View<FULL_CHILD_T, CHILD_T> view,
                Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>, NC>> childrenSelector
        ) {
            super(t -> adapt(selector.apply(t)), childrenSelector);
        }

        void takeThese(Node<PARENT_T, T> node) {
            if (childrenSelector == null)
                node.consumeAll(selector);
            else
                node.consumeAll(selector, childrenSelector);
        }
    }

    final class ChildArrayOf<PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>>
            extends MultiChild<PARENT_T, T, CHILD_T, CHILD_T[], NC> {

        ChildArrayOf(
                Function<T, CHILD_T[]> selector
        ) {
            super(selector);
        }

        ChildArrayOf(
                Function<T, CHILD_T[]> selector,
                Consumer<MultiNode<T, CHILD_T, CHILD_T[], NC>> childrenSelector
        ) {
            super(selector, childrenSelector);
        }

        <FULL_CHILD_T extends CHILD_T>
        ChildArrayOf(
                Function<T, FULL_CHILD_T[]> selector,
                View<FULL_CHILD_T, CHILD_T> view
        ) {
            super(selector::apply);
        }

        <FULL_CHILD_T extends CHILD_T>
        ChildArrayOf(
                Function<T, FULL_CHILD_T[]> selector,
                View<FULL_CHILD_T, CHILD_T> view,
                Consumer<MultiNode<T, CHILD_T, CHILD_T[], NC>> childrenSelector
        ) {
            super(selector::apply, childrenSelector);
        }

        void takeThese(Node<PARENT_T, T> node) {
            if (childrenSelector == null)
                node.accessAll(selector);
            else
                node.accessAll(selector, childrenSelector);
        }
    }

    static <PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>>
    ChildStreamOf<PARENT_T, T, CHILD_T, NC> stream_Of(Function<T, Stream<CHILD_T>> selector) {
        return new ChildStreamOf<>
                (
                        selector
                );
    }

    static <PARENT_T, T, CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    ChildStreamOf<PARENT_T, T, CHILD_T_VIEW, NC> stream_Of(Function<T, Stream<CHILD_T>> selector,
                                                           View<CHILD_T, CHILD_T_VIEW> view) {
        return new ChildStreamOf<>
                (
                        selector,
                        view
                );
    }

    static <PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>>
    ChildStreamOf<PARENT_T, T, CHILD_T, NC> stream_Of(Function<T, Stream<CHILD_T>> selector,
                                                      Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>, NC>> childrenSelector) {
        return new ChildStreamOf<>
                (
                        selector,
                        childrenSelector
                );
    }

    static <PARENT_T, T, CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    ChildStreamOf<PARENT_T, T, CHILD_T_VIEW, NC> stream_Of(Function<T, Stream<CHILD_T>> selector,
                                                           View<CHILD_T, CHILD_T_VIEW> view,
                                                           Consumer<MultiNode<T, CHILD_T_VIEW, Stream<CHILD_T_VIEW>, NC>> childrenSelector) {
        return new ChildStreamOf<>
                (
                        selector,
                        view,
                        childrenSelector
                );
    }

    static <PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>>
    ChildIterableOf<PARENT_T, T, CHILD_T, NC> collection_Of(Function<T, Iterable<CHILD_T>> selector) {
        return new ChildIterableOf<>
                (
                        selector
                );
    }

    static <PARENT_T, T, CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    ChildIterableOf<PARENT_T, T, CHILD_T_VIEW, NC> collection_Of(Function<T, Iterable<CHILD_T>> selector,
                                                                 View<CHILD_T, CHILD_T_VIEW> view) {
        return new ChildIterableOf<>
                (
                        selector,
                        view
                );
    }

    static <PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>>
    ChildIterableOf<PARENT_T, T, CHILD_T, NC> collection_Of(Function<T, Iterable<CHILD_T>> selector,
                                                            Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>, NC>> childrenSelector) {
        return new ChildIterableOf<>
                (
                        selector,
                        childrenSelector
                );
    }

    static <PARENT_T, T, CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    ChildIterableOf<PARENT_T, T, CHILD_T_VIEW, NC> collection_Of(Function<T, Iterable<CHILD_T>> selector,
                                                                 View<CHILD_T, CHILD_T_VIEW> view,
                                                                 Consumer<MultiNode<T, CHILD_T_VIEW, Iterable<CHILD_T_VIEW>, NC>> childrenSelector) {
        return new ChildIterableOf<>
                (
                        selector,
                        view,
                        childrenSelector
                );
    }

    static <PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>>
    ChildArrayOf<PARENT_T, T, CHILD_T, NC> array_Of(Function<T, CHILD_T[]> selector) {
        return new ChildArrayOf<>
                (
                        selector
                );
    }

    static <PARENT_T, T, CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    ChildArrayOf<PARENT_T, T, CHILD_T_VIEW, NC> array_Of(Function<T, CHILD_T[]> selector,
                                                         View<CHILD_T, CHILD_T_VIEW> view) {
        return new ChildArrayOf<>
                (
                        selector,
                        view
                );
    }

    static <PARENT_T, T, CHILD_T, NC extends Consumer<Node<T, CHILD_T>>>
    ChildArrayOf<PARENT_T, T, CHILD_T, NC> array_Of(Function<T, CHILD_T[]> selector,
                                                    Consumer<MultiNode<T, CHILD_T, CHILD_T[], NC>> childrenSelector) {
        return new ChildArrayOf<>
                (
                        selector,
                        childrenSelector
                );
    }

    static <PARENT_T, T, CHILD_T extends CHILD_T_VIEW, CHILD_T_VIEW, NC extends Consumer<Node<T, CHILD_T_VIEW>>>
    ChildArrayOf<PARENT_T, T, CHILD_T_VIEW, NC> array_Of(Function<T, CHILD_T[]> selector,
                                                         View<CHILD_T, CHILD_T_VIEW> view,
                                                         Consumer<MultiNode<T, CHILD_T_VIEW, CHILD_T_VIEW[], NC>> childrenSelector) {
        return new ChildArrayOf<>
                (
                        selector,
                        view,
                        childrenSelector
                );
    }

    @SafeVarargs
    static <PARENT_T, T>
    SingularChild<PARENT_T, T, ?, ?>[] associated(SingularChild<PARENT_T, T, ?, ?>... children) {
        return children;
    }

    @SafeVarargs
    static <PARENT_T, T>
    SingularChild<PARENT_T, T, ?, ?>[] the_Associated(SingularChild<PARENT_T, T, ?, ?>... children) {
        return children;
    }

    @SafeVarargs
    static <PARENT_T, T>
    SingularChild<PARENT_T, T, ?, ?>[] its_Associated(SingularChild<PARENT_T, T, ?, ?>... children) {
        return children;
    }

    @SafeVarargs
    static <PARENT_T, T>
    SingularChild<PARENT_T, T, ?, ?>[] his_Associated(SingularChild<PARENT_T, T, ?, ?>... children) {
        return children;
    }

    @SafeVarargs
    static <PARENT_T, T>
    SingularChild<PARENT_T, T, ?, ?>[] her_Associated(SingularChild<PARENT_T, T, ?, ?>... children) {
        return children;
    }

    @SafeVarargs
    static <PARENT_T, T>
    SingularChild<PARENT_T, T, ?, ?>[] their_Associated(SingularChild<PARENT_T, T, ?, ?>... children) {
        return children;
    }

    @SafeVarargs
    static <PARENT_T, T>
    MultiChild<PARENT_T, T, ?, ?, ?>[] contained(MultiChild<PARENT_T, T, ?, ?, ?>... children) {
        return children;
    }

    @SafeVarargs
    static <PARENT_T, T>
    MultiChild<PARENT_T, T, ?, ?, ?>[] the_Contained(MultiChild<PARENT_T, T, ?, ?, ?>... children) {
        return children;
    }

    @SafeVarargs
    static <PARENT_T, T>
    MultiChild<PARENT_T, T, ?, ?, ?>[] its_Contained(MultiChild<PARENT_T, T, ?, ?, ?>... children) {
        return children;
    }

    @SafeVarargs
    static <PARENT_T, T>
    MultiChild<PARENT_T, T, ?, ?, ?>[] his_Contained(MultiChild<PARENT_T, T, ?, ?, ?>... children) {
        return children;
    }

    @SafeVarargs
    static <PARENT_T, T>
    MultiChild<PARENT_T, T, ?, ?, ?>[] her_Contained(MultiChild<PARENT_T, T, ?, ?, ?>... children) {
        return children;
    }

    @SafeVarargs
    static <PARENT_T, T>
    MultiChild<PARENT_T, T, ?, ?, ?>[] their_Contained(MultiChild<PARENT_T, T, ?, ?, ?>... children) {
        return children;
    }

    static <PARENT_T, T, N extends Node<PARENT_T, T>>
    Consumer<N> include
            (
                    SingularChild<PARENT_T, T, ?, ?>[] singularChildren
            ) {
        return n -> {
            for (SingularChild<PARENT_T, T, ?, ?> child : singularChildren) {
                child.takeThe(n);
            }
        };
    }

    static <PARENT_T, T, N extends Node<PARENT_T, T>>
    Consumer<N> include
            (
                    MultiChild<PARENT_T, T, ?, ?, ?>[] pluralChildren
            ) {
        return n -> {
            for (MultiChild<PARENT_T, T, ?, ?, ?> child : pluralChildren) {
                child.takeThese(n);
            }
        };
    }

    static <PARENT_T, T, N extends Node<PARENT_T, T>>
    Consumer<N> include
            (
                    SingularChild<PARENT_T, T, ?, ?>[] singularChildren,
                    MultiChild<PARENT_T, T, ?, ?, ?>[] pluralChildren
            ) {
        return n -> {
            include(singularChildren).accept(n);
            include(pluralChildren).accept(n);
        };
    }

    @SuppressWarnings("FieldCanBeLocal")
    class VisitConfiguration<PARENT_T, T, NC extends Consumer<Node<PARENT_T, T>>>
            implements NodeVisitorConfiguration<PARENT_T, T, NC> {

        private VisitHandler<PARENT_T, T> beforeSelf;
        private VisitHandler<PARENT_T, T> beforeAllChildren;
        private VisitHandler<PARENT_T, T> beforeEachChild;
        private VisitHandler<PARENT_T, T> afterEachChild;
        private VisitHandler<PARENT_T, T> afterAllChildren;
        private VisitHandler<PARENT_T, T> afterSelf;

        <N extends NodeVisitorConfiguration<PARENT_T, T, NC>> void configure(N n) {
            if (beforeSelf != null) n.beforeSelf(beforeSelf);
            if (beforeAllChildren != null) n.beforeAllChildren(beforeAllChildren);
            if (beforeEachChild != null) n.beforeEachChild(beforeEachChild);
            if (afterEachChild != null) n.afterEachChild(afterEachChild);
            if (afterAllChildren != null) n.afterAllChildren(afterAllChildren);
            if (afterSelf != null) n.afterSelf(afterSelf);
        }

        @Override
        public NodeVisitorBeforeAllChildrenConfiguration<PARENT_T, T, NC> beforeSelf(VisitHandler<PARENT_T, T> handler) {
            this.beforeSelf = handler;
            return this;
        }

        @Override
        public NodeVisitorBeforeEachChildConfiguration<PARENT_T, T, NC> beforeAllChildren(VisitHandler<PARENT_T, T> handler) {
            this.beforeAllChildren = handler;
            return this;
        }

        @Override
        public NodeVisitorAfterEachChildConfiguration<PARENT_T, T, NC> beforeEachChild(VisitHandler<PARENT_T, T> handler) {
            this.beforeEachChild = handler;
            return this;
        }

        @Override
        public NodeVisitorAfterAllChildrenConfiguration<PARENT_T, T, NC> afterEachChild(VisitHandler<PARENT_T, T> handler) {
            this.afterEachChild = handler;
            return this;
        }

        @Override
        public NodeVisitorAfterSelfConfiguration<PARENT_T, T, NC> afterAllChildren(VisitHandler<PARENT_T, T> handler) {
            this.afterAllChildren = handler;
            return this;
        }

        @Override
        public NodeVisitorConfigurationFinalizer<PARENT_T, T, NC> afterSelf(VisitHandler<PARENT_T, T> handler) {
            this.afterSelf = handler;
            return this;
        }
    }

    class MultiVisitConfiguration<PARENT_T, T, CT, NC extends Consumer<Node<PARENT_T, T>>>
            extends VisitConfiguration<PARENT_T, T, NC>
            implements MultiNodeVisitorConfiguration<PARENT_T, T, CT, NC> {

        private VisitHandler<PARENT_T, CT> beforeAll;
        private VisitHandler<PARENT_T, CT> afterAll;

        <N extends MultiNodeVisitorConfiguration<PARENT_T, T, CT, NC>> void configure(N n) {
            if (beforeAll != null) n.beforeAll(beforeAll);
            if (afterAll != null) n.afterAll(afterAll);
        }

        @Override
        public MultiNodeAfterCollectionVisitorConfiguration<PARENT_T, T, CT, NC> beforeAll(VisitHandler<PARENT_T, CT> handler) {
            this.beforeAll = handler;
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T, NC> afterAll(VisitHandler<PARENT_T, CT> handler) {
            this.afterAll = handler;
            return this;
        }

    }

    static <PARENT_T, T, N extends SingleNode<PARENT_T, T, NC>, NC extends Consumer<Node<PARENT_T, T>>>
    VisitConfiguration<PARENT_T, T, NC> singularVisitConfiguration() {
        return new VisitConfiguration<>();
    }

    static <PARENT_T, T, CT, N extends MultiNode<PARENT_T, T, CT, NC>, NC extends Consumer<Node<PARENT_T, T>>>
    MultiVisitConfiguration<PARENT_T, T, CT, NC> multiVisitConfiguration() {
        return new MultiVisitConfiguration<>();
    }

    static <PARENT_T, T, N extends SingleNode<PARENT_T, T, NC>, NC extends Consumer<Node<PARENT_T, T>>>
    Consumer<N> withVisitConfiguredAs
            (
                    VisitConfiguration<PARENT_T, T, NC> visitConfiguration,
                    Consumer<N> selector
            ) {
        return n -> {
            //noinspection unchecked
            visitConfiguration.configure((NodeVisitorConfiguration<PARENT_T, T, NC>) n);
            selector.accept(n);
        };
    }

    static <PARENT_T, T, CT, N extends MultiNode<PARENT_T, T, CT, NC>, NC extends Consumer<Node<PARENT_T, T>>>
    Consumer<N> withCollectionVisitsConfiguredAs
            (
                    MultiVisitConfiguration<PARENT_T, T, CT, NC> visitConfiguration,
                    Consumer<N> selector
            ) {
        return n -> {
            //noinspection unchecked
            visitConfiguration.configure((MultiNodeVisitorConfiguration<PARENT_T, T, CT, NC>) n);
            selector.accept(n);
        };
    }

    @Deprecated
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

    @Deprecated
    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N with(Function<T, CHILD_T> selector) {
        return with
                (
                        selector,
                        (Consumer<SingleNode<T, CHILD_T, NC>>) null
                );
    }

    @Deprecated
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

    @Deprecated
    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N with(Function<T, CHILD_T> selector,
           Consumer<SingleNode<T, CHILD_T, NC>> childrenSelector);


    @Deprecated
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

    @Deprecated
    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N selectAll(Function<T, Stream<CHILD_T>> selector) {
        return selectAll
                (
                        selector,
                        (Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>, NC>>) null
                );
    }

    @Deprecated
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

    @Deprecated
    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N selectAll(Function<T, Stream<CHILD_T>> selector,
                Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>, NC>> childrenSelector);


    @Deprecated
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

    @Deprecated
    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N consumeAll(Function<T, Iterable<CHILD_T>> selector) {
        return consumeAll
                (
                        selector,
                        (Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>, NC>>) null
                );
    }

    @Deprecated
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

    @Deprecated
    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N consumeAll(Function<T, Iterable<CHILD_T>> selector,
                 Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>, NC>> childrenSelector);


    @Deprecated
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

    @Deprecated
    default <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N accessAll(Function<T, CHILD_T[]> selector) {
        return accessAll
                (
                        selector,
                        (Consumer<MultiNode<T, CHILD_T, CHILD_T[], NC>>) null
                );
    }

    @Deprecated
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

    @Deprecated
    <CHILD_T, N extends Node<PARENT_T, T>, NC extends Consumer<Node<T, CHILD_T>>>
    N accessAll(Function<T, CHILD_T[]> selector,
                Consumer<MultiNode<T, CHILD_T, CHILD_T[], NC>> childrenSelector);
}