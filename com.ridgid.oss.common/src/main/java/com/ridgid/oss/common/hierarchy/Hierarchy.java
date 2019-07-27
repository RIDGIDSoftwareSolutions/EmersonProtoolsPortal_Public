package com.ridgid.oss.common.hierarchy;

import com.ridgid.oss.common.callback.HandlerList;

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

    public void visit(PARENT_T parent, Consumer<Object> visitor, Traversal traversal) {
        UUID key = UUID.randomUUID();
        try {
            built.visit(key, parent, visitor, traversal);
        } finally {
            SAVED_STATE.remove(key);
        }
    }

    public enum Traversal {
        DEPTH_FIRST,
        BREADTH_FIRST
    }

    private static ConcurrentMap<UUID, ConcurrentMap<VisitableNode, Object>> SAVED_STATE = new ConcurrentHashMap<>();

    private interface VisitableNode<PARENT_T, CHILD_T> extends Node<CHILD_T> {
        VisitStatus visit(UUID traversalKey, PARENT_T parent, Consumer<Object> visitor, Traversal traversal);

        VisitStatus visitOnlySelf(UUID traversalKey, PARENT_T parent, Consumer<Object> visitor);
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
                                           Consumer<Node<CHILD_T>> childrenSelector) {
            SingleChild<T, CHILD_T> child = new SingleChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> Builder<T> many(Function<T, Stream<CHILD_T>> selector) {
            return many(selector, null);
        }

        public <CHILD_T> Builder<T> many(Function<T, Stream<CHILD_T>> selector,
                                         Consumer<Node<CHILD_T>> childrenSelector) {
            StreamChild<T, CHILD_T> child = new StreamChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> Builder<T> collection(Function<T, Iterable<CHILD_T>> selector) {
            return collection(selector, null);
        }

        public <CHILD_T> Builder<T> collection(Function<T, Iterable<CHILD_T>> selector,
                                               Consumer<Node<CHILD_T>> childrenSelector) {
            IterableChild<T, CHILD_T> child = new IterableChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> Builder<T> array(Function<T, CHILD_T[]> selector) {
            return array(selector, null);
        }

        public <CHILD_T> Builder<T> array(Function<T, CHILD_T[]> selector,
                                          Consumer<Node<CHILD_T>> childrenSelector) {
            ArrayChild<T, CHILD_T> child = new ArrayChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public Hierarchy<T> build() {
            return new Hierarchy<>(this);
        }

        @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
        private void visit(UUID key, T obj, Consumer<Object> visitor, Traversal traversal) {
            visitor.accept(obj);
            if (traversal.equals(BREADTH_FIRST))
                if (childNodes
                        .stream()
                        .map(child -> child.visitOnlySelf(key, obj, visitor))
                        .anyMatch(VisitStatus::isSkipSiblings))
                    return;
            childNodes
                    .stream()
                    .map(child -> child.visit(key, obj, visitor, traversal))
                    .anyMatch(VisitStatus::isSkipSiblings);
        }
    }

    private static abstract class BaseNode<PARENT_T, T> implements VisitableNode<PARENT_T, T>, NodeVisitorConfiguration<T> {

        protected List<VisitableNode> childNodes;
        protected HandlerList<T, VisitStatus> beforeVisitHandlers;
        protected HandlerList<T, VisitStatus> afterVisitHandlers;
        protected HandlerList<T, VisitStatus> beforeEachChildVisitHandlers;
        protected HandlerList<T, VisitStatus> afterEachChildVisitHandlers;
        protected HandlerList<T, VisitStatus> beforeAllChildrenVisitHandlers;
        protected HandlerList<T, VisitStatus> afterAllChildrenVisitHandlers;

        private void addChild(VisitableNode child) {
            if (childNodes == null) childNodes = new ArrayList<>();
            childNodes.add(child);
        }

        @Override
        public <CHILD_T> Node<T> single(Function<T, CHILD_T> selector,
                                        Consumer<Node<CHILD_T>> childrenSelector) {
            SingleChild<T, CHILD_T> child = new SingleChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return this;
        }

        @Override
        public <CHILD_T> Node<T> many(Function<T, Stream<CHILD_T>> selector,
                                      Consumer<Node<CHILD_T>> childrenSelector) {
            StreamChild<T, CHILD_T> child = new StreamChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return this;
        }

        @Override
        public <CHILD_T> Node<T> collection(Function<T, Iterable<CHILD_T>> selector,
                                            Consumer<Node<CHILD_T>> childrenSelector) {
            IterableChild<T, CHILD_T> child = new IterableChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return this;
        }

        @Override
        public <CHILD_T> Node<T> array(Function<T, CHILD_T[]> selector,
                                       Consumer<Node<CHILD_T>> childrenSelector) {
            ArrayChild<T, CHILD_T> child = new ArrayChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return this;
        }

        @Override
        public void whenVisited(Consumer<NodeVisitorConfiguration<T>> visitConfigurer) {
            visitConfigurer.accept(this);
        }

        @Override
        public NodeVisitorConfiguration<T> before(VisitHandler<T> handler) {
            if (beforeVisitHandlers == null) beforeVisitHandlers = new HandlerList<>();
            beforeVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<T> after(VisitHandler<T> handler) {
            if (afterVisitHandlers == null) afterVisitHandlers = new HandlerList<>();
            afterVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<T> beforeEachChild(VisitHandler<T> handler) {
            if (beforeEachChildVisitHandlers == null) beforeEachChildVisitHandlers = new HandlerList<>();
            beforeEachChildVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<T> afterEachChild(VisitHandler<T> handler) {
            if (afterEachChildVisitHandlers == null) afterEachChildVisitHandlers = new HandlerList<>();
            afterEachChildVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<T> beforeAllChildren(VisitHandler<T> handler) {
            if (beforeAllChildrenVisitHandlers == null) beforeAllChildrenVisitHandlers = new HandlerList<>();
            beforeAllChildrenVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<T> afterAllChildren(VisitHandler<T> handler) {
            if (afterAllChildrenVisitHandlers == null) afterAllChildrenVisitHandlers = new HandlerList<>();
            afterAllChildrenVisitHandlers.add(handler);
            return this;
        }

        protected final VisitStatus visit(UUID key,
                                          Consumer<Object> visitor,
                                          Traversal traversal,
                                          T self) {
            if (self != null) {
                if (traversal.equals(DEPTH_FIRST)) {
                    VisitStatus vs = visitStart(key, visitor, self);
                    if (vs.isSkipNode()) return vs;
                }
                VisitStatus vs = visitChildren(key, visitor, traversal, self);
                if (vs.isSkipNode()) return vs;
            }
            return visitEnd(key, self);
        }

        protected final VisitStatus visitStart(UUID key,
                                               Consumer<Object> visitor,
                                               T self) {
            if (self == null) return CONTINUE_PROCESSING;
            VisitStatus vs = invokeVisitHandlers(self, beforeVisitHandlers);
            if (vs.isSkipNode()) return vs;
            visitor.accept(self);
            return CONTINUE_PROCESSING;
        }

        private VisitStatus invokeVisitHandlers(T self,
                                                HandlerList<T, VisitStatus> visitHandlers) {
            return visitHandlers == null
                    ? CONTINUE_PROCESSING
                    : visitHandlers
                    .invoke(self, VisitStatus::isNotOk)
                    .orElse(CONTINUE_PROCESSING);
        }

        private VisitStatus visitChildren(UUID key,
                                          Consumer<Object> visitor,
                                          Traversal traversal, T self) {
            VisitStatus vs
                    = invokeVisitHandlers(self, beforeAllChildrenVisitHandlers);
            if (vs.isSkipNode()) return vs;
            vs = visitEachChild(key, visitor, traversal, self);
            if (vs.isSkipNode()) return vs;

            return invokeVisitHandlers(self, afterAllChildrenVisitHandlers);
        }

        @SuppressWarnings("unchecked")
        private VisitStatus visitEachChild(UUID key,
                                           Consumer<Object> visitor,
                                           Traversal traversal,
                                           T self) {
            if (childNodes == null) return CONTINUE_PROCESSING;

            HashSet<VisitableNode> includedChildren
                    = (HashSet<VisitableNode>) SAVED_STATE
                    .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(this, k -> new HashSet<VisitableNode>());
            includedChildren.clear();

            if (traversal.equals(BREADTH_FIRST))
                for (VisitableNode childNode : childNodes) {
                    VisitStatus vs = invokeVisitHandlers(self, beforeEachChildVisitHandlers);
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
                VisitStatus vs = invokeVisitHandlers(self, beforeEachChildVisitHandlers);
                if (vs.isStop()) return vs;
                if (vs.isSkipSiblings()) break;
                if (vs.isSkipNode()) continue;

                vs = childNode.visit(key, self, visitor, traversal);
                if (vs.isStop()) return vs;
                if (vs.isSkipSiblings()) break;
                if (vs.isSkipNode()) continue;

                vs = invokeVisitHandlers(self, afterEachChildVisitHandlers);
                if (vs.isStop()) return vs;
                if (vs.isSkipSiblings()) break;
            }
            return CONTINUE_PROCESSING;
        }

        private VisitStatus visitEnd(UUID key,
                                     T self) {
            return invokeVisitHandlers(self, afterVisitHandlers);
        }

    }

    private static class SingleChild<PARENT_T, CHILD_T> extends BaseNode<PARENT_T, CHILD_T> {

        private final Function<PARENT_T, CHILD_T> childSelector;

        private SingleChild(Function<PARENT_T, CHILD_T> childSelector) {
            this.childSelector = childSelector;
        }

        @Override
        public VisitStatus visit(UUID key, PARENT_T parent, Consumer<Object> visitor, Traversal traversal) {
            CHILD_T self = childSelector.apply(parent);
            return visit(key, visitor, traversal, self);
        }

        @Override
        public VisitStatus visitOnlySelf(UUID key, PARENT_T parent, Consumer<Object> visitor) {
            CHILD_T self = childSelector.apply(parent);
            return visitStart(key, visitor, self);
        }

    }

    private static abstract class ManyBaseNode<PARENT_T, CHILD_T> extends BaseNode<PARENT_T, CHILD_T> {

        @SuppressWarnings("unchecked")
        protected final VisitStatus visit(UUID key,
                                          Consumer<Object> visitor,
                                          Traversal traversal,
                                          Iterator<CHILD_T> it) {
            HashSet<CHILD_T> includedEntries
                    = (HashSet<CHILD_T>) SAVED_STATE
                    .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(this, k -> new HashSet<CHILD_T>());
            while (it.hasNext()) {
                CHILD_T self = it.next();
                if (traversal.equals(BREADTH_FIRST) && !includedEntries.contains(self)) continue;
                VisitStatus vs = visit(key, visitor, traversal, self);
                if (vs.isStop()) return vs;
                if (vs.isSkipSiblings()) return CONTINUE_PROCESSING;
            }
            return CONTINUE_PROCESSING;
        }

        @SuppressWarnings("unchecked")
        protected final VisitStatus visitOnlySelf(UUID key,
                                                  Consumer<Object> visitor,
                                                  Iterator<CHILD_T> it) {
            HashSet<CHILD_T> includedEntries
                    = (HashSet<CHILD_T>) SAVED_STATE
                    .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(this, k -> new HashSet<CHILD_T>());
            includedEntries.clear();
            while (it.hasNext()) {
                CHILD_T self = it.next();
                VisitStatus vs = visitStart(key, visitor, self);
                if (vs.isStop()) return vs;
                if (vs.isSkipSiblings()) break;
                if (vs.isSkipNode()) continue;
                includedEntries.add(self);
            }
            return CONTINUE_PROCESSING;
        }

    }

    private static class StreamChild<PARENT_T, CHILD_T> extends ManyBaseNode<PARENT_T, CHILD_T> {

        private final Function<PARENT_T, Stream<CHILD_T>> childSelector;

        public StreamChild(Function<PARENT_T, Stream<CHILD_T>> selector) {
            this.childSelector = selector;
        }

        @Override
        public VisitStatus visit(UUID key,
                                 PARENT_T parent,
                                 Consumer<Object> visitor,
                                 Traversal traversal) {
            Iterator<CHILD_T> it = childSelector.apply(parent).iterator();
            return visit(key, visitor, traversal, it);
        }

        @Override
        public VisitStatus visitOnlySelf(UUID key,
                                         PARENT_T parent,
                                         Consumer<Object> visitor) {
            Iterator<CHILD_T> it = childSelector.apply(parent).iterator();
            return visitOnlySelf(key, visitor, it);
        }

    }

    private static class IterableChild<PARENT_T, CHILD_T> extends ManyBaseNode<PARENT_T, CHILD_T> {

        private final Function<PARENT_T, Iterable<CHILD_T>> childSelector;

        public IterableChild(Function<PARENT_T, Iterable<CHILD_T>> childSelector) {
            this.childSelector = childSelector;
        }

        @Override
        public VisitStatus visit(UUID key,
                                 PARENT_T parent, Consumer<Object> visitor,
                                 Traversal traversal) {
            Iterator<CHILD_T> it = childSelector.apply(parent).iterator();
            return visit(key, visitor, traversal, it);
        }

        @Override
        public VisitStatus visitOnlySelf(UUID key,
                                         PARENT_T parent,
                                         Consumer<Object> visitor) {
            Iterator<CHILD_T> it = childSelector.apply(parent).iterator();
            return visitOnlySelf(key, visitor, it);
        }
    }

    private static class ArrayChild<PARENT_T, CHILD_T> extends ManyBaseNode<PARENT_T, CHILD_T> {

        private final Function<PARENT_T, CHILD_T[]> childSelector;

        public ArrayChild(Function<PARENT_T, CHILD_T[]> childSelector) {
            this.childSelector = childSelector;
        }

        @Override
        public VisitStatus visit(UUID key,
                                 PARENT_T parent,
                                 Consumer<Object> visitor,
                                 Traversal traversal) {
            Iterator<CHILD_T> it = Arrays.stream(childSelector.apply(parent)).iterator();
            return visit(key, visitor, traversal, it);
        }

        @Override
        public VisitStatus visitOnlySelf(UUID key,
                                         PARENT_T parent,
                                         Consumer<Object> visitor) {
            Iterator<CHILD_T> it = Arrays.stream(childSelector.apply(parent)).iterator();
            return visitOnlySelf(key, visitor, it);
        }
    }
}
