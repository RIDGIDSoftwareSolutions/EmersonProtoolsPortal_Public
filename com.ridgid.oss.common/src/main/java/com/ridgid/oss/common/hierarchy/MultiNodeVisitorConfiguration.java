package com.ridgid.oss.common.hierarchy;

@SuppressWarnings({"unused"})
public interface MultiNodeVisitorConfiguration<PARENT_T, T, CT>
        extends NodeVisitorConfiguration<PARENT_T, T> {
    MultiNodeVisitorConfiguration<PARENT_T, T, CT> beforeVisitingAny(VisitHandler<PARENT_T, CT> handler);

    MultiNodeVisitorConfiguration<PARENT_T, T, CT> afterVisitingAll(VisitHandler<PARENT_T, CT> handler);
}
