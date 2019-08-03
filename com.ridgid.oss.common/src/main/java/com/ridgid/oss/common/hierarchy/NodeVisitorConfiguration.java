package com.ridgid.oss.common.hierarchy;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface NodeVisitorConfiguration<PARENT_T, T> {

    NodeVisitorConfiguration<PARENT_T, T> beforeVisitingSelf(VisitHandler<PARENT_T, T> handler);

    NodeVisitorConfiguration<PARENT_T, T> beforeVisitingAllChildren(VisitHandler<PARENT_T, T> handler);

    NodeVisitorConfiguration<PARENT_T, T> beforeVisitingEachChild(VisitHandler<PARENT_T, T> handler);

    NodeVisitorConfiguration<PARENT_T, T> afterVisitingEachChild(VisitHandler<PARENT_T, T> handler);

    NodeVisitorConfiguration<PARENT_T, T> afterVisitingAllChildren(VisitHandler<PARENT_T, T> handler);

    NodeVisitorConfiguration<PARENT_T, T> afterVisitingSelf(VisitHandler<PARENT_T, T> handler);

}
