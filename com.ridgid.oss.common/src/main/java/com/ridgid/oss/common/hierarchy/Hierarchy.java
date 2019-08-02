package com.ridgid.oss.common.hierarchy;

import com.ridgid.oss.common.callback.BiHandlerList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.ridgid.oss.common.hierarchy.Hierarchy.Traversal.BREADTH_FIRST;
import static com.ridgid.oss.common.hierarchy.Hierarchy.Traversal.DEPTH_FIRST;
import static com.ridgid.oss.common.hierarchy.VisitStatus.CONTINUE_PROCESSING;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Hierarchy<PARENT_T> {

    private final Builder<PARENT_T> built;

    private Hierarchy(Builder<PARENT_T> built) {
        this.built = built;
    }

    public static <T> Builder<T> root(Class<T> rootClass) {
        return new Builder<>(rootClass);
    }

    public void visit(PARENT_T parent,
                      GeneralVisitHandler visitor,
                      Traversal traversal) {
        visit(parent,
                visitor,
                null,
                traversal);
    }

    public void visit(PARENT_T parent,
                      GeneralVisitHandler beforeChildrenVisitor,
                      GeneralVisitHandler afterChildrenVisitor,
                      Traversal traversal) {
        UUID key = UUID.randomUUID();
        try {
            built.visit(
                    key,
                    parent,
                    beforeChildrenVisitor,
                    Optional.ofNullable(afterChildrenVisitor),
                    traversal);
        } finally {
            SAVED_STATE.remove(key);
        }
    }

    public enum Traversal {
        DEPTH_FIRST,
        BREADTH_FIRST
    }

    private static ConcurrentMap<UUID, ConcurrentMap<VisitableNode, Object>> SAVED_STATE = new ConcurrentHashMap<>();

    private interface VisitableNode<PARENT_T, T> extends Node<PARENT_T, T> {
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        VisitStatus visit(UUID traversalKey,
                          PARENT_T parent,
                          VisitHandler<Object, Object> visitor,
                          Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                          Traversal traversal);

        VisitStatus visitOnlySelf(UUID traversalKey,
                                  PARENT_T parent,
                                  VisitHandler<Object, Object> visitor);
    }

    @SuppressWarnings("FieldCanBeLocal")
    public static class Builder<T> {

        private final Class<T> rootClass;

        private List<VisitableNode> childNodes = new ArrayList<>();

        private Builder(Class<T> rootClass) {
            this.rootClass = rootClass;
        }

        public <CHILD_T> Builder<T> single(Function<T, CHILD_T> selector) {
            return single(selector, null);
        }

        public <CHILD_T> Builder<T> single(Function<T, CHILD_T> selector,
                                           Consumer<SingleNode<T, CHILD_T>> childrenSelector) {
            SingleChild<T, CHILD_T> child = new SingleChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> Builder<T> many(Function<T, Stream<CHILD_T>> selector) {
            return many(selector, null);
        }

        public <CHILD_T> Builder<T> many(Function<T, Stream<CHILD_T>> selector,
                                         Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>>> childrenSelector) {
            StreamChild<T, CHILD_T> child = new StreamChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> Builder<T> collection(Function<T, Iterable<CHILD_T>> selector) {
            return collection(selector, null);
        }

        public <CHILD_T> Builder<T> collection(Function<T, Iterable<CHILD_T>> selector,
                                               Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>>> childrenSelector) {
            IterableChild<T, CHILD_T> child = new IterableChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> Builder<T> array(Function<T, CHILD_T[]> selector) {
            return array(selector, null);
        }

        public <CHILD_T> Builder<T> array(Function<T, CHILD_T[]> selector,
                                          Consumer<MultiNode<T, CHILD_T, CHILD_T[]>> childrenSelector) {
            ArrayChild<T, CHILD_T> child = new ArrayChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public Hierarchy<T> build() {
            return new Hierarchy<>(this);
        }

        @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored", "OptionalUsedAsFieldOrParameterType"})
        private void visit(UUID key,
                           T obj,
                           VisitHandler<Object, Object> visitor,
                           Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                           Traversal traversal) {
            if (visitor.handle(null, obj).isNotOk()) return;
            if (traversal.equals(BREADTH_FIRST))
                if (childNodes
                        .stream()
                        .map(child -> child.visitOnlySelf(key, obj, visitor))
                        .anyMatch(VisitStatus::isSkipSiblings))
                    return;
            childNodes
                    .stream()
                    .map(child -> child.visit(key, obj, visitor, afterChildrenVisitor, traversal))
                    .anyMatch(VisitStatus::isSkipSiblings);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static abstract class BaseNode<PARENT_T, T>
            implements
            VisitableNode<PARENT_T, T>,
            NodeVisitorConfiguration<PARENT_T, T> {

        protected List<VisitableNode> childNodes;
        protected BiHandlerList<PARENT_T, T, VisitStatus> beforeVisitHandlers;
        protected BiHandlerList<PARENT_T, T, VisitStatus> afterVisitHandlers;
        protected BiHandlerList<PARENT_T, T, VisitStatus> beforeEachChildVisitHandlers;
        protected BiHandlerList<PARENT_T, T, VisitStatus> afterEachChildVisitHandlers;
        protected BiHandlerList<PARENT_T, T, VisitStatus> beforeAllChildrenVisitHandlers;
        protected BiHandlerList<PARENT_T, T, VisitStatus> afterAllChildrenVisitHandlers;

        private void addChild(VisitableNode child) {
            if (childNodes == null) childNodes = new ArrayList<>();
            childNodes.add(child);
        }

        @Override
        public <CHILD_T> Node<PARENT_T, T> single(Function<T, CHILD_T> selector,
                                                  Consumer<SingleNode<T, CHILD_T>> childrenSelector) {
            SingleChild<T, CHILD_T> child = new SingleChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return this;
        }

        @Override
        public <CHILD_T> Node<PARENT_T, T> many(Function<T, Stream<CHILD_T>> selector,
                                                Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>>> childrenSelector) {
            StreamChild<T, CHILD_T> child = new StreamChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return this;
        }

        @Override
        public <CHILD_T> Node<PARENT_T, T> collection(Function<T, Iterable<CHILD_T>> selector,
                                                      Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>>> childrenSelector) {
            IterableChild<T, CHILD_T> child = new IterableChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return this;
        }

        @Override
        public <CHILD_T> Node<PARENT_T, T> array(Function<T, CHILD_T[]> selector,
                                                 Consumer<MultiNode<T, CHILD_T, CHILD_T[]>> childrenSelector) {
            ArrayChild<T, CHILD_T> child = new ArrayChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> before(VisitHandler<PARENT_T, T> handler) {
            if (beforeVisitHandlers == null) beforeVisitHandlers = new BiHandlerList<>();
            beforeVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> after(VisitHandler<PARENT_T, T> handler) {
            if (afterVisitHandlers == null) afterVisitHandlers = new BiHandlerList<>();
            afterVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> beforeEachChild(VisitHandler<PARENT_T, T> handler) {
            if (beforeEachChildVisitHandlers == null) beforeEachChildVisitHandlers = new BiHandlerList<>();
            beforeEachChildVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> afterEachChild(VisitHandler<PARENT_T, T> handler) {
            if (afterEachChildVisitHandlers == null) afterEachChildVisitHandlers = new BiHandlerList<>();
            afterEachChildVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> beforeAllChildren(VisitHandler<PARENT_T, T> handler) {
            if (beforeAllChildrenVisitHandlers == null) beforeAllChildrenVisitHandlers = new BiHandlerList<>();
            beforeAllChildrenVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> afterAllChildren(VisitHandler<PARENT_T, T> handler) {
            if (afterAllChildrenVisitHandlers == null) afterAllChildrenVisitHandlers = new BiHandlerList<>();
            afterAllChildrenVisitHandlers.add(handler);
            return this;
        }

        private VisitStatus invokeVisitHandlers(PARENT_T parent,
                                                T self,
                                                BiHandlerList<PARENT_T, T, VisitStatus> visitHandlers) {
            return visitHandlers == null
                    ? CONTINUE_PROCESSING
                    : visitHandlers
                    .invoke(parent, self, VisitStatus::isNotOk)
                    .orElse(CONTINUE_PROCESSING);
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        protected final VisitStatus visit(UUID key,
                                          VisitHandler<Object, Object> visitor,
                                          Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                                          Traversal traversal,
                                          PARENT_T parent,
                                          T self) {
            if (self != null) {
                if (traversal.equals(DEPTH_FIRST)) {
                    VisitStatus vs = visitStart(key, visitor, parent, self);
                    if (vs.isSkipNode()) return vs;
                }
                VisitStatus vs = visitChildren(key, visitor, afterChildrenVisitor, traversal, parent, self);
                if (vs.isSkipNode()) return vs;
            }
            return visitEnd(key, afterChildrenVisitor, parent, self);
        }

        protected final VisitStatus visitStart(UUID key,
                                               VisitHandler<Object, Object> visitor,
                                               PARENT_T parent,
                                               T self) {
            if (self == null) return CONTINUE_PROCESSING;
            VisitStatus vs = invokeVisitHandlers(parent, self, beforeVisitHandlers);
            if (vs.isSkipNode()) return vs;
            return visitor.handle(parent, self);
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private VisitStatus visitChildren(UUID key,
                                          VisitHandler<Object, Object> visitor,
                                          Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                                          Traversal traversal,
                                          PARENT_T parent,
                                          T self) {
            VisitStatus vs
                    = invokeVisitHandlers(parent, self, beforeAllChildrenVisitHandlers);
            if (vs.isSkipNode()) return vs;
            vs = visitEachChild(key, visitor, afterChildrenVisitor, traversal, parent, self);
            if (vs.isSkipNode()) return vs;
            return invokeVisitHandlers(parent, self, afterAllChildrenVisitHandlers);
        }

        @SuppressWarnings("unchecked")
        private VisitStatus visitEachChild(UUID key,
                                           VisitHandler<Object, Object> visitor,
                                           Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                                           Traversal traversal,
                                           PARENT_T parent,
                                           T self) {
            if (childNodes == null) return CONTINUE_PROCESSING;

            HashSet<VisitableNode> includedChildren
                    = (HashSet<VisitableNode>) SAVED_STATE
                    .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(this, k -> new HashSet<VisitableNode>());
            includedChildren.clear();

            if (traversal.equals(BREADTH_FIRST))
                for (VisitableNode childNode : childNodes) {
                    VisitStatus vs = invokeVisitHandlers(parent, self, beforeEachChildVisitHandlers);
                    if (vs.isStop()) return vs;
                    if (vs.isSkipSiblings()) break;
                    if (vs.isSkipNode()) continue;
                    vs = childNode.visitOnlySelf(key, self, visitor);
                    if (vs.isStop()) return vs;
                    if (vs.isSkipSiblings()) break;
                    if (vs.isSkipNode()) continue;
                    includedChildren.add(childNode);
                }

            for (VisitableNode childNode : childNodes) {
                if (traversal.equals(BREADTH_FIRST) && !includedChildren.contains(childNode)) continue;
                if (traversal.equals(DEPTH_FIRST)) {
                    VisitStatus vs = invokeVisitHandlers(parent, self, beforeEachChildVisitHandlers);
                    if (vs.isStop()) return vs;
                    if (vs.isSkipSiblings()) break;
                    if (vs.isSkipNode()) continue;
                }
                VisitStatus vs = childNode.visit(key, self, visitor, afterChildrenVisitor, traversal);
                if (vs.isStop()) return vs;
                if (vs.isSkipSiblings()) break;
                if (vs.isSkipNode()) continue;
                vs = invokeVisitHandlers(parent, self, afterEachChildVisitHandlers);
                if (vs.isStop()) return vs;
                if (vs.isSkipSiblings()) break;
            }
            return CONTINUE_PROCESSING;
        }

        private VisitStatus visitEnd(UUID key,
                                     Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                                     PARENT_T parent,
                                     T self) {
            if (self == null) return CONTINUE_PROCESSING;
            VisitStatus vs
                    = afterChildrenVisitor
                    .map(visitor -> visitor.handle(parent, self))
                    .orElse(CONTINUE_PROCESSING);
            if (vs.isSkipNode()) return vs;
            return invokeVisitHandlers(parent, self, afterVisitHandlers);
        }

    }

    private static class SingleChild<PARENT_T, T>
            extends
            BaseNode<PARENT_T, T>
            implements SingleNode<PARENT_T, T> {

        private final Function<PARENT_T, T> childSelector;

        private SingleChild(Function<PARENT_T, T> childSelector) {
            this.childSelector = childSelector;
        }

        @Override
        public VisitStatus visit(UUID key,
                                 PARENT_T parent,
                                 VisitHandler<Object, Object> visitor,
                                 Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                                 Traversal traversal) {
            T self = childSelector.apply(parent);
            return visit(key, visitor, afterChildrenVisitor, traversal, parent, self);
        }

        @Override
        public VisitStatus visitOnlySelf(UUID key, PARENT_T parent, VisitHandler<Object, Object> visitor) {
            T self = childSelector.apply(parent);
            return visitStart(key, visitor, parent, self);
        }

        @Override
        public void whenVisited(Consumer<NodeVisitorConfiguration<PARENT_T, T>> visitConfigurer) {
            visitConfigurer.accept(this);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static abstract class ManyBaseNode<PARENT_T, T, CT>
            extends
            BaseNode<PARENT_T, T>
            implements
            MultiNode<PARENT_T, T, CT>,
            MultiNodeVisitorConfiguration<PARENT_T, T, CT> {

        protected BiHandlerList<PARENT_T, CT, VisitStatus> beforeManyVisitHandlers;
        protected BiHandlerList<PARENT_T, CT, VisitStatus> afterManyVisitHandlers;

        protected VisitStatus invokeVisitHandlers(PARENT_T parent,
                                                  CT collection,
                                                  BiHandlerList<PARENT_T, CT, VisitStatus> visitHandlers) {
            return visitHandlers == null
                    ? CONTINUE_PROCESSING
                    : visitHandlers
                    .invoke(parent, collection, VisitStatus::isNotOk)
                    .orElse(CONTINUE_PROCESSING);
        }

        @Override
        public MultiNodeVisitorConfiguration<PARENT_T, T, CT> beforeMany(VisitHandler<PARENT_T, CT> handler) {
            if (beforeManyVisitHandlers == null) beforeManyVisitHandlers = new BiHandlerList<>();
            beforeManyVisitHandlers.add(handler);
            return this;
        }

        @Override
        public MultiNodeVisitorConfiguration<PARENT_T, T, CT> afterMany(VisitHandler<PARENT_T, CT> handler) {
            if (afterManyVisitHandlers == null) afterManyVisitHandlers = new BiHandlerList<>();
            afterManyVisitHandlers.add(handler);
            return this;
        }

        @Override
        public void whenVisited(Consumer<MultiNodeVisitorConfiguration<PARENT_T, T, CT>> visitConfigurer) {
            visitConfigurer.accept(this);
        }

        @SuppressWarnings("unchecked")
        protected final VisitStatus visit(UUID key,
                                          PARENT_T parent,
                                          VisitHandler<Object, Object> visitor,
                                          Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                                          Traversal traversal,
                                          CT collection,
                                          Iterator<T> it) {
            if (traversal.equals(DEPTH_FIRST)) {
                VisitStatus vs = visitCollection(key, parent, visitor, collection);
                if (vs.isStop()) return vs;
                if (vs.isSkipSiblings()) return vs;
                if (vs.isSkipNode()) return CONTINUE_PROCESSING;
            }
            HashSet<T> includedEntries
                    = (HashSet<T>) SAVED_STATE
                    .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(this, k -> new HashSet<T>());
            while (it.hasNext()) {
                T self = it.next();
                if (traversal.equals(BREADTH_FIRST) && !includedEntries.contains(self)) continue;
                VisitStatus vs = visit(key, visitor, afterChildrenVisitor, traversal, parent, self);
                if (vs.isStop()) return vs;
                if (vs.isSkipSiblings()) return CONTINUE_PROCESSING;
            }
            return invokeVisitHandlers(parent, collection, afterManyVisitHandlers);
        }

        @SuppressWarnings({"unchecked", "DuplicatedCode"})
        protected final VisitStatus visitOnlySelf(UUID key,
                                                  PARENT_T parent,
                                                  VisitHandler<Object, Object> visitor,
                                                  CT collection,
                                                  Iterator<T> it) {
            VisitStatus vs = visitCollection(key, parent, visitor, collection);
            if (vs.isStop()) return vs;
            if (vs.isSkipSiblings()) return vs;
            if (vs.isSkipNode()) return CONTINUE_PROCESSING;
            HashSet<T> includedEntries
                    = (HashSet<T>) SAVED_STATE
                    .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(this, k -> new HashSet<T>());
            includedEntries.clear();
            while (it.hasNext()) {
                T self = it.next();
                vs = visitStart(key, visitor, parent, self);
                if (vs.isStop()) return vs;
                if (vs.isSkipSiblings()) break;
                if (vs.isSkipNode()) continue;
                includedEntries.add(self);
            }
            return CONTINUE_PROCESSING;
        }

        private VisitStatus visitCollection(UUID key,
                                            PARENT_T parent,
                                            VisitHandler<Object, Object> visitor,
                                            CT collection) {
            VisitStatus vs = invokeVisitHandlers(parent, collection, beforeManyVisitHandlers);
            if (vs.isSkipNode()) return vs;
            return visitor.handle(parent, collection);
        }
    }

    private static class StreamChild<PARENT_T, T>
            extends ManyBaseNode<PARENT_T, T, Stream<T>> {

        private final Function<PARENT_T, Stream<T>> childSelector;

        public StreamChild(Function<PARENT_T, Stream<T>> selector) {
            this.childSelector = selector;
        }

        @Override
        public final VisitStatus visit(UUID key,
                                       PARENT_T parent,
                                       VisitHandler<Object, Object> visitor,
                                       Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                                       Traversal traversal) {
            try (Stream<T> stream = childSelector.apply(parent)) {
                return visit(
                        key,
                        parent,
                        visitor,
                        afterChildrenVisitor,
                        traversal,
                        stream,
                        stream.iterator());
            }
        }

        @Override
        public final VisitStatus visitOnlySelf(UUID key,
                                               PARENT_T parent,
                                               VisitHandler<Object, Object> visitor) {
            try (Stream<T> stream = childSelector.apply(parent)) {
                return visitOnlySelf(
                        key,
                        parent,
                        visitor,
                        stream,
                        stream.iterator());
            }
        }
    }

    private static class IterableChild<PARENT_T, T>
            extends ManyBaseNode<PARENT_T, T, Iterable<T>> {

        private final Function<PARENT_T, Iterable<T>> childSelector;

        public IterableChild(Function<PARENT_T, Iterable<T>> childSelector) {
            this.childSelector = childSelector;
        }

        @Override
        public final VisitStatus visit(UUID key,
                                       PARENT_T parent,
                                       VisitHandler<Object, Object> visitor,
                                       Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                                       Traversal traversal) {
            Iterable<T> it = childSelector.apply(parent);
            return visit(
                    key,
                    parent,
                    visitor,
                    afterChildrenVisitor,
                    traversal,
                    it,
                    it.iterator());
        }

        @Override
        public final VisitStatus visitOnlySelf(UUID key,
                                               PARENT_T parent,
                                               VisitHandler<Object, Object> visitor) {
            Iterable<T> it = childSelector.apply(parent);
            return visitOnlySelf(
                    key,
                    parent,
                    visitor,
                    it,
                    it.iterator());
        }
    }

    private static class ArrayChild<PARENT_T, T>
            extends ManyBaseNode<PARENT_T, T, T[]> {

        private final Function<PARENT_T, T[]> childSelector;

        public ArrayChild(Function<PARENT_T, T[]> childSelector) {
            this.childSelector = childSelector;
        }

        @Override
        public final VisitStatus visit(UUID key,
                                       PARENT_T parent,
                                       VisitHandler<Object, Object> visitor,
                                       Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                                       Traversal traversal) {
            T[] array = childSelector.apply(parent);
            return visit(
                    key,
                    parent,
                    visitor,
                    afterChildrenVisitor,
                    traversal,
                    array,
                    Arrays.stream(array).iterator());
        }

        @Override
        public final VisitStatus visitOnlySelf(UUID key,
                                               PARENT_T parent,
                                               VisitHandler<Object, Object> visitor) {
            T[] array = childSelector.apply(parent);
            return visitOnlySelf(
                    key,
                    parent,
                    visitor,
                    array,
                    Arrays.stream(array).iterator());
        }
    }
}
