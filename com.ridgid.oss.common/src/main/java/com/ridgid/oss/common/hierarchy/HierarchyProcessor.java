package com.ridgid.oss.common.hierarchy;

import com.ridgid.oss.common.callback.BiHandlerList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.ridgid.oss.common.hierarchy.HierarchyProcessor.Traversal.BREADTH_FIRST;
import static com.ridgid.oss.common.hierarchy.HierarchyProcessor.Traversal.DEPTH_FIRST;
import static com.ridgid.oss.common.hierarchy.VisitStatus.OK_CONTINUE;

@SuppressWarnings({"unused", "WeakerAccess"})
public class HierarchyProcessor<PARENT_T> {

    private final HierarchyProcessorBuilder<PARENT_T> built;

    private HierarchyProcessor(HierarchyProcessorBuilder<PARENT_T> built) {
        this.built = built;
    }

    public static <T> HierarchyProcessorBuilder<T> from(Class<T> rootClass) {
        return new HierarchyProcessorBuilder<>(rootClass);
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
    public static class HierarchyProcessorBuilder<T> {

        private final Class<T> rootClass;

        private List<VisitableNode> childNodes = new ArrayList<>();

        private HierarchyProcessorBuilder(Class<T> rootClass) {
            this.rootClass = rootClass;
        }

        public <CHILD_T> HierarchyProcessorBuilder<T> use(Function<T, CHILD_T> selector) {
            return use(selector, null);
        }

        public <CHILD_T> HierarchyProcessorBuilder<T> use(Function<T, CHILD_T> selector,
                                                          Consumer<SingleNode<T, CHILD_T>> childrenSelector) {
            SingleChild<T, CHILD_T> child = new SingleChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> HierarchyProcessorBuilder<T> selectAll(Function<T, Stream<CHILD_T>> selector) {
            return selectAll(selector, null);
        }

        public <CHILD_T> HierarchyProcessorBuilder<T> selectAll(Function<T, Stream<CHILD_T>> selector,
                                                                Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>>> childrenSelector) {
            StreamChild<T, CHILD_T> child = new StreamChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> HierarchyProcessorBuilder<T> consumeAll(Function<T, Iterable<CHILD_T>> selector) {
            return consumeAll(selector, null);
        }

        public <CHILD_T> HierarchyProcessorBuilder<T> consumeAll(Function<T, Iterable<CHILD_T>> selector,
                                                                 Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>>> childrenSelector) {
            IterableChild<T, CHILD_T> child = new IterableChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public <CHILD_T> HierarchyProcessorBuilder<T> accessAll(Function<T, CHILD_T[]> selector) {
            return accessAll(selector, null);
        }

        public <CHILD_T> HierarchyProcessorBuilder<T> accessAll(Function<T, CHILD_T[]> selector,
                                                                Consumer<MultiNode<T, CHILD_T, CHILD_T[]>> childrenSelector) {
            ArrayChild<T, CHILD_T> child = new ArrayChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            childNodes.add(child);
            return this;
        }

        public HierarchyProcessor<T> buildProcessor() {
            return new HierarchyProcessor<>(this);
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

        @SuppressWarnings("unchecked")
        @Override
        public <CHILD_T, N extends Node<PARENT_T, T>> N use(Function<T, CHILD_T> selector,
                                                            Consumer<SingleNode<T, CHILD_T>> childrenSelector) {
            SingleChild<T, CHILD_T> child = new SingleChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return (N) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <CHILD_T, N extends Node<PARENT_T, T>> N selectAll(Function<T, Stream<CHILD_T>> selector,
                                                                  Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>>> childrenSelector) {
            StreamChild<T, CHILD_T> child = new StreamChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return (N) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <CHILD_T, N extends Node<PARENT_T, T>> N consumeAll(Function<T, Iterable<CHILD_T>> selector,
                                                                   Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>>> childrenSelector) {
            IterableChild<T, CHILD_T> child = new IterableChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return (N) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <CHILD_T, N extends Node<PARENT_T, T>> N accessAll(Function<T, CHILD_T[]> selector,
                                                                  Consumer<MultiNode<T, CHILD_T, CHILD_T[]>> childrenSelector) {
            ArrayChild<T, CHILD_T> child = new ArrayChild<>(selector);
            if (childrenSelector != null) childrenSelector.accept(child);
            addChild(child);
            return (N) this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> beforeVisitingSelf(VisitHandler<PARENT_T, T> handler) {
            if (beforeVisitHandlers == null) beforeVisitHandlers = new BiHandlerList<>();
            beforeVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> afterVisitingSelf(VisitHandler<PARENT_T, T> handler) {
            if (afterVisitHandlers == null) afterVisitHandlers = new BiHandlerList<>();
            afterVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> beforeVisitingEachChild(VisitHandler<PARENT_T, T> handler) {
            if (beforeEachChildVisitHandlers == null) beforeEachChildVisitHandlers = new BiHandlerList<>();
            beforeEachChildVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> afterVisitingEachChild(VisitHandler<PARENT_T, T> handler) {
            if (afterEachChildVisitHandlers == null) afterEachChildVisitHandlers = new BiHandlerList<>();
            afterEachChildVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> beforeVisitingAllChildren(VisitHandler<PARENT_T, T> handler) {
            if (beforeAllChildrenVisitHandlers == null) beforeAllChildrenVisitHandlers = new BiHandlerList<>();
            beforeAllChildrenVisitHandlers.add(handler);
            return this;
        }

        @Override
        public NodeVisitorConfiguration<PARENT_T, T> afterVisitingAllChildren(VisitHandler<PARENT_T, T> handler) {
            if (afterAllChildrenVisitHandlers == null) afterAllChildrenVisitHandlers = new BiHandlerList<>();
            afterAllChildrenVisitHandlers.add(handler);
            return this;
        }

        private VisitStatus invokeVisitHandlers(PARENT_T parent,
                                                T self,
                                                BiHandlerList<PARENT_T, T, VisitStatus> visitHandlers) {
            return visitHandlers == null
                    ? OK_CONTINUE
                    : visitHandlers
                    .invoke(parent, self, VisitStatus::isNotOk)
                    .orElse(OK_CONTINUE);
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
            if (self == null) return OK_CONTINUE;
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
            if (childNodes == null) return OK_CONTINUE;

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
            return OK_CONTINUE;
        }

        private VisitStatus visitEnd(UUID key,
                                     Optional<VisitHandler<Object, Object>> afterChildrenVisitor,
                                     PARENT_T parent,
                                     T self) {
            if (self == null) return OK_CONTINUE;
            VisitStatus vs
                    = afterChildrenVisitor
                    .map(visitor -> visitor.handle(parent, self))
                    .orElse(OK_CONTINUE);
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
        public <CHILD_T, N extends Node<PARENT_T, T>> N use(Function<T, CHILD_T> selector, Consumer<SingleNode<T, CHILD_T>> childrenSelector) {
            return super.use(selector, childrenSelector);
        }

        @Override
        public <CHILD_T, N extends Node<PARENT_T, T>> N selectAll(Function<T, Stream<CHILD_T>> selector, Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>>> childrenSelector) {
            return super.selectAll(selector, childrenSelector);
        }

        @Override
        public <CHILD_T, N extends Node<PARENT_T, T>> N consumeAll(Function<T, Iterable<CHILD_T>> selector, Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>>> childrenSelector) {
            return super.consumeAll(selector, childrenSelector);
        }

        @Override
        public <CHILD_T, N extends Node<PARENT_T, T>> N accessAll(Function<T, CHILD_T[]> selector, Consumer<MultiNode<T, CHILD_T, CHILD_T[]>> childrenSelector) {
            return super.accessAll(selector, childrenSelector);
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
        public Node<PARENT_T, T> onVisit(Consumer<NodeVisitorConfiguration<PARENT_T, T>> visitConfigurer) {
            visitConfigurer.accept(this);
            return this;
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
                    ? OK_CONTINUE
                    : visitHandlers
                    .invoke(parent, collection, VisitStatus::isNotOk)
                    .orElse(OK_CONTINUE);
        }

        @Override
        public <CHILD_T, N extends Node<PARENT_T, T>> N use(Function<T, CHILD_T> selector, Consumer<SingleNode<T, CHILD_T>> childrenSelector) {
            return super.use(selector, childrenSelector);
        }

        @Override
        public <CHILD_T, N extends Node<PARENT_T, T>> N selectAll(Function<T, Stream<CHILD_T>> selector, Consumer<MultiNode<T, CHILD_T, Stream<CHILD_T>>> childrenSelector) {
            return super.selectAll(selector, childrenSelector);
        }

        @Override
        public <CHILD_T, N extends Node<PARENT_T, T>> N consumeAll(Function<T, Iterable<CHILD_T>> selector, Consumer<MultiNode<T, CHILD_T, Iterable<CHILD_T>>> childrenSelector) {
            return super.consumeAll(selector, childrenSelector);
        }

        @Override
        public <CHILD_T, N extends Node<PARENT_T, T>> N accessAll(Function<T, CHILD_T[]> selector, Consumer<MultiNode<T, CHILD_T, CHILD_T[]>> childrenSelector) {
            return super.accessAll(selector, childrenSelector);
        }

        @Override
        public MultiNodeVisitorConfiguration<PARENT_T, T, CT> beforeVisitingAny(VisitHandler<PARENT_T, CT> handler) {
            if (beforeManyVisitHandlers == null) beforeManyVisitHandlers = new BiHandlerList<>();
            beforeManyVisitHandlers.add(handler);
            return this;
        }

        @Override
        public MultiNodeVisitorConfiguration<PARENT_T, T, CT> afterVisitingAll(VisitHandler<PARENT_T, CT> handler) {
            if (afterManyVisitHandlers == null) afterManyVisitHandlers = new BiHandlerList<>();
            afterManyVisitHandlers.add(handler);
            return this;
        }

        @Override
        public Node<PARENT_T, T> onVisit(Consumer<MultiNodeVisitorConfiguration<PARENT_T, T, CT>> visitConfigurer) {
            visitConfigurer.accept(this);
            return this;
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
                if (vs.isSkipNode()) return OK_CONTINUE;
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
                if (vs.isSkipSiblings()) return OK_CONTINUE;
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
            if (vs.isSkipNode()) return OK_CONTINUE;
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
            return OK_CONTINUE;
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
