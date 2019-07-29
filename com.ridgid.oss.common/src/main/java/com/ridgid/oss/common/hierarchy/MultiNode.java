package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface MultiNode<PARENT_T, T, CT>
        extends Node<PARENT_T, T> {
    void whenVisited(Consumer<MultiNodeVisitorConfiguration<PARENT_T, T, CT>> visitConfigurer);
}