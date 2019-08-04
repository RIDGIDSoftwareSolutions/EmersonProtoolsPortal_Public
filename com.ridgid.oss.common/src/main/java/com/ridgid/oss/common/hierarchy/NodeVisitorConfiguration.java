package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public interface NodeVisitorConfiguration<PARENT_T, T, NC extends Consumer<Node<PARENT_T, T>>>
        extends NodeVisitorBeforeSelfConfiguration<PARENT_T, T, NC> {
}

interface NodeVisitorBeforeSelfConfiguration<PARENT_T, T, NC extends Consumer<Node<PARENT_T, T>>>
        extends NodeVisitorBeforeAllChildrenConfiguration<PARENT_T, T, NC> {
    @SuppressWarnings("unused")
    NodeVisitorBeforeAllChildrenConfiguration<PARENT_T, T, NC> beforeSelf(VisitHandler<PARENT_T, T> handler);
}

interface NodeVisitorBeforeAllChildrenConfiguration<PARENT_T, T, NC extends Consumer<Node<PARENT_T, T>>>
        extends NodeVisitorBeforeEachChildConfiguration<PARENT_T, T, NC> {
    @SuppressWarnings("UnusedReturnValue")
    NodeVisitorBeforeEachChildConfiguration<PARENT_T, T, NC> beforeAllChildren(VisitHandler<PARENT_T, T> handler);
}

interface NodeVisitorBeforeEachChildConfiguration<PARENT_T, T, NC extends Consumer<Node<PARENT_T, T>>>
        extends NodeVisitorAfterEachChildConfiguration<PARENT_T, T, NC> {
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    NodeVisitorAfterEachChildConfiguration<PARENT_T, T, NC> beforeEachChild(VisitHandler<PARENT_T, T> handler);
}

interface NodeVisitorAfterEachChildConfiguration<PARENT_T, T, NC extends Consumer<Node<PARENT_T, T>>>
        extends NodeVisitorAfterAllChildrenConfiguration<PARENT_T, T, NC> {
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    NodeVisitorAfterAllChildrenConfiguration<PARENT_T, T, NC> afterEachChild(VisitHandler<PARENT_T, T> handler);
}

interface NodeVisitorAfterAllChildrenConfiguration<PARENT_T, T, NC extends Consumer<Node<PARENT_T, T>>>
        extends NodeVisitorAfterSelfConfiguration<PARENT_T, T, NC> {
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    NodeVisitorAfterSelfConfiguration<PARENT_T, T, NC> afterAllChildren(VisitHandler<PARENT_T, T> handler);
}

interface NodeVisitorAfterSelfConfiguration<PARENT_T, T, NC extends Consumer<Node<PARENT_T, T>>>
        extends NodeVisitorConfigurationFinalizer<PARENT_T, T, NC> {
    @SuppressWarnings("unused")
    NodeVisitorConfigurationFinalizer<PARENT_T, T, NC> afterSelf(VisitHandler<PARENT_T, T> handler);
}

@SuppressWarnings("unused")
interface NodeVisitorConfigurationFinalizer<PARENT_T, T, NC extends Consumer<Node<PARENT_T, T>>> {
    @SuppressWarnings("unused")
    default <CONFIG_T> CONFIG_T applyConfiguration() {
        //noinspection unchecked
        return (CONFIG_T) this;
    }
}
