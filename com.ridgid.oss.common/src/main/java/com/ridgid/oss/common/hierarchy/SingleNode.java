package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface SingleNode<PARENT_T, T>
        extends Node<PARENT_T, T> {
    Node<PARENT_T, T> whenVisited(Consumer<NodeVisitorConfiguration<PARENT_T, T>> visitConfigurer);
}