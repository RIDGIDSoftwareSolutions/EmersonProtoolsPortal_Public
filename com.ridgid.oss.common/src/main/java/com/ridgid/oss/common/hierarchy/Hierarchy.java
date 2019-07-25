package com.ridgid.oss.common.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.ridgid.oss.common.hierarchy.Hierarchy.Traversal.BREADTH_FIRST;
import static com.ridgid.oss.common.hierarchy.Hierarchy.Traversal.DEPTH_FIRST;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Hierarchy<PARENT_T> {

    private final Builder<PARENT_T> built;

    private Hierarchy(Builder<PARENT_T> built) {
        this.built = built;
    }

    public static <T> Builder<T> root(Class<T> rootClass) {
        return new Builder<>(rootClass);
    }

    public void visit(PARENT_T parent, Consumer<Object> visitor, Traversal traversal) {
        built.visit(parent, visitor, traversal);
    }

    public enum Traversal {
        DEPTH_FIRST,
        BREADTH_FIRST
    }

    private interface VisitableNode<PARENT_T, CHILD_T> extends Node<CHILD_T> {
        void visit(PARENT_T parent, Consumer<Object> visitor, Traversal traversal);

        void visitOnlySelf(PARENT_T parent, Consumer<Object> visitor);
    }

    @SuppressWarnings("FieldCanBeLocal")
    public static class Builder<T> {

        private final Class<T> rootClass;

        private List<VisitableNode> childNodes = new ArrayList<>();

        private Builder(Class<T> rootClass) {
            this.rootClass = rootClass;
        }

        public <CHILD_T> Builder<T> hasOne(Function<T, CHILD_T> selector) {
            return hasOne(selector, null);
        }

        public <CHILD_T> Builder<T> hasOne(Function<T, CHILD_T> selector,
                                           Consumer<Node<CHILD_T>> childrenSelector) {
            SingleChild<T, CHILD_T> child = new SingleChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> Builder<T> hasMany(Function<T, Stream<CHILD_T>> selector) {
            return hasMany(selector, null);
        }

        public <CHILD_T> Builder<T> hasMany(Function<T, Stream<CHILD_T>> selector,
                                            Consumer<Node<CHILD_T>> childrenSelector) {
            StreamChild<T, CHILD_T> child = new StreamChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> Builder<T> hasMany_i(Function<T, Iterable<CHILD_T>> selector) {
            return hasMany_i(selector, null);
        }

        public <CHILD_T> Builder<T> hasMany_i(Function<T, Iterable<CHILD_T>> selector,
                                              Consumer<Node<CHILD_T>> childrenSelector) {
            IterableChild<T, CHILD_T> child = new IterableChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> Builder<T> hasMany_a(Function<T, CHILD_T[]> selector) {
            return hasMany_a(selector, null);
        }

        public <CHILD_T> Builder<T> hasMany_a(Function<T, CHILD_T[]> selector,
                                              Consumer<Node<CHILD_T>> childrenSelector) {
            ArrayChild<T, CHILD_T> child = new ArrayChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public Hierarchy<T> build() {
            return new Hierarchy<>(this);
        }

        @SuppressWarnings("unchecked")
        private void visit(T obj, Consumer<Object> visitor, Traversal traversal) {
            visitor.accept(obj);
            if (traversal.equals(BREADTH_FIRST))
                childNodes.forEach(child -> child.visitOnlySelf(obj, visitor));
            childNodes.forEach(child -> child.visit(obj, visitor, traversal));
        }
    }

    private static abstract class BaseNode<PARENT_T, T> implements VisitableNode<PARENT_T, T> {

        protected List<VisitableNode> childNodes = new ArrayList<>();

        @Override
        public <CHILD_T> Node<T> hasOne(Function<T, CHILD_T> selector,
                                        Consumer<Node<CHILD_T>> childrenSelector) {
            SingleChild<T, CHILD_T> child = new SingleChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        @Override
        public <CHILD_T> Node<T> hasMany(Function<T, Stream<CHILD_T>> selector,
                                         Consumer<Node<CHILD_T>> childrenSelector) {
            StreamChild<T, CHILD_T> child = new StreamChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        @Override
        public <CHILD_T> Node<T> hasMany_i(Function<T, Iterable<CHILD_T>> selector,
                                           Consumer<Node<CHILD_T>> childrenSelector) {
            IterableChild<T, CHILD_T> child = new IterableChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        @Override
        public <CHILD_T> Node<T> hasMany_a(Function<T, CHILD_T[]> selector,
                                           Consumer<Node<CHILD_T>> childrenSelector) {
            ArrayChild<T, CHILD_T> child = new ArrayChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

    }

    private static class SingleChild<PARENT_T, CHILD_T> extends BaseNode<PARENT_T, CHILD_T> {

        private final Function<PARENT_T, CHILD_T> childSelector;

        private SingleChild(Function<PARENT_T, CHILD_T> childSelector) {
            this.childSelector = childSelector;
        }


        @SuppressWarnings("unchecked")
        @Override
        public void visit(PARENT_T parent, Consumer<Object> visitor, Traversal traversal) {
            CHILD_T child = childSelector.apply(parent);
            if (child != null) {
                if (traversal.equals(DEPTH_FIRST))
                    visitor.accept(child);
                if (traversal.equals(BREADTH_FIRST))
                    childNodes.forEach(childNode -> childNode.visitOnlySelf(child, visitor));
                childNodes.forEach(childNode -> childNode.visit(child, visitor, traversal));
            }
        }

        @Override
        public void visitOnlySelf(PARENT_T parent, Consumer<Object> visitor) {
            CHILD_T child = childSelector.apply(parent);
            if (child != null)
                visitor.accept(child);
        }
    }

    private static class StreamChild<PARENT_T, CHILD_T> extends BaseNode<PARENT_T, CHILD_T> {

        private final Function<PARENT_T, Stream<CHILD_T>> childSelector;

        public StreamChild(Function<PARENT_T, Stream<CHILD_T>> selector) {
            this.childSelector = selector;
        }

        @SuppressWarnings({"unchecked", "LambdaBodyCanBeCodeBlock"})
        @Override
        public void visit(PARENT_T parent, Consumer<Object> visitor, Traversal traversal) {
            if (traversal.equals(DEPTH_FIRST))
                childSelector
                        .apply(parent)
                        .peek(visitor)
                        .forEach(child -> childNodes.forEach(childNode -> childNode.visit(child, visitor, traversal)));
            else {
                childSelector
                        .apply(parent)
                        .forEach(child -> childNodes.forEach(childNode -> childNode.visitOnlySelf(child, visitor)));
                childSelector
                        .apply(parent)
                        .forEach(child -> childNodes.forEach(childNode -> childNode.visit(child, visitor, traversal)));
            }
        }

        @Override
        public void visitOnlySelf(PARENT_T parent, Consumer<Object> visitor) {
            childSelector
                    .apply(parent)
                    .forEach(visitor);
        }
    }

    private static class IterableChild<PARENT_T, CHILD_T> extends BaseNode<PARENT_T, CHILD_T> {

        private final Function<PARENT_T, Iterable<CHILD_T>> childSelector;

        public IterableChild(Function<PARENT_T, Iterable<CHILD_T>> childSelector) {
            this.childSelector = childSelector;
        }

        @SuppressWarnings({"unchecked", "LambdaBodyCanBeCodeBlock"})
        @Override
        public void visit(PARENT_T parent, Consumer<Object> visitor, Traversal traversal) {
            Iterable<CHILD_T> children = childSelector.apply(parent);
            if (children != null)
                for (CHILD_T child : children) {
                    if (traversal.equals(DEPTH_FIRST)) visitor.accept(child);
                    if (traversal.equals(BREADTH_FIRST))
                        childNodes.forEach(childNode -> childNode.visitOnlySelf(child, visitor));
                    childNodes.forEach(childNode -> childNode.visit(child, visitor, traversal));
                }
        }

        @Override
        public void visitOnlySelf(PARENT_T parent, Consumer<Object> visitor) {
            childSelector
                    .apply(parent)
                    .forEach(visitor);
        }
    }

    private static class ArrayChild<PARENT_T, CHILD_T> extends BaseNode<PARENT_T, CHILD_T> {

        private final Function<PARENT_T, CHILD_T[]> childSelector;

        public ArrayChild(Function<PARENT_T, CHILD_T[]> childSelector) {
            this.childSelector = childSelector;
        }

        @SuppressWarnings({"unchecked", "LambdaBodyCanBeCodeBlock", "Duplicates"})
        @Override
        public void visit(PARENT_T parent, Consumer<Object> visitor, Traversal traversal) {
            CHILD_T[] children = childSelector.apply(parent);
            for (CHILD_T child : children) {
                if (traversal.equals(DEPTH_FIRST)) visitor.accept(child);
                if (traversal.equals(BREADTH_FIRST))
                    childNodes.forEach(childNode -> childNode.visitOnlySelf(child, visitor));
                childNodes.forEach(childNode -> childNode.visit(child, visitor, traversal));
            }
        }

        @Override
        public void visitOnlySelf(PARENT_T parent, Consumer<Object> visitor) {
            CHILD_T[] children = childSelector.apply(parent);
            for (CHILD_T child : children)
                visitor.accept(child);
        }
    }
}
