package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface MultiNode<PARENT_T, T, CT, NC extends Consumer<Node<PARENT_T, T>>>
        extends Node<PARENT_T, T> {
    Node<PARENT_T, T> onVisit(Consumer<MultiNodeVisitorConfiguration<PARENT_T, T, CT, NC>> visitConfigurer);
}