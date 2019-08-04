package com.ridgid.oss.common.hierarchy;

import java.util.function.Consumer;

@SuppressWarnings({"unused", "WeakerAccess"})
public interface MultiNodeVisitorConfiguration<PARENT_T, T, CT, NC extends Consumer<Node<PARENT_T, T>>>
        extends MultiNodeBeforeCollectionVisitorConfiguration<PARENT_T, T, CT, NC> {
}

interface MultiNodeBeforeCollectionVisitorConfiguration<PARENT_T, T, CT, NC extends Consumer<Node<PARENT_T, T>>>
        extends MultiNodeAfterCollectionVisitorConfiguration<PARENT_T, T, CT, NC> {
    @SuppressWarnings("unused")
    MultiNodeAfterCollectionVisitorConfiguration<PARENT_T, T, CT, NC> beforeAll(VisitHandler<PARENT_T, CT> handler);
}

interface MultiNodeAfterCollectionVisitorConfiguration<PARENT_T, T, CT, NC extends Consumer<Node<PARENT_T, T>>>
        extends NodeVisitorConfiguration<PARENT_T, T, NC> {
    NodeVisitorConfiguration<PARENT_T, T, NC> afterAll(VisitHandler<PARENT_T, CT> handler);
}
