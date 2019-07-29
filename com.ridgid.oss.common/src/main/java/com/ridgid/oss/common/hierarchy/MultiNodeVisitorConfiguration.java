package com.ridgid.oss.common.hierarchy;

@SuppressWarnings({"unused"})
public interface MultiNodeVisitorConfiguration<PARENT_T, T, CT>
        extends NodeVisitorConfiguration<PARENT_T, T> {
    MultiNodeVisitorConfiguration<PARENT_T, T, CT> beforeMany(VisitHandler<PARENT_T, CT> handler);

    MultiNodeVisitorConfiguration<PARENT_T, T, CT> afterMany(VisitHandler<PARENT_T, CT> handler);
}
