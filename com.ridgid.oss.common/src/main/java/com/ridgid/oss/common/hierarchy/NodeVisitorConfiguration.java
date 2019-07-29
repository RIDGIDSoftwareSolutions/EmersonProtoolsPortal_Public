package com.ridgid.oss.common.hierarchy;

@SuppressWarnings({"unused"})
public interface NodeVisitorConfiguration<PARENT_T, T> {
    NodeVisitorConfiguration<PARENT_T, T> before(VisitHandler<PARENT_T, T> handler);

    NodeVisitorConfiguration<PARENT_T, T> after(VisitHandler<PARENT_T, T> handler);

    NodeVisitorConfiguration<PARENT_T, T> beforeEachChild(VisitHandler<PARENT_T, T> handler);

    NodeVisitorConfiguration<PARENT_T, T> afterEachChild(VisitHandler<PARENT_T, T> handler);

    NodeVisitorConfiguration<PARENT_T, T> beforeAllChildren(VisitHandler<PARENT_T, T> handler);

    NodeVisitorConfiguration<PARENT_T, T> afterAllChildren(VisitHandler<PARENT_T, T> handler);
}
