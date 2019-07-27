package com.ridgid.oss.common.hierarchy;

@SuppressWarnings({"unused"})
public interface NodeVisitorConfiguration<T> {
    NodeVisitorConfiguration<T> before(VisitHandler<T> handler);

    NodeVisitorConfiguration<T> after(VisitHandler<T> handler);

    NodeVisitorConfiguration<T> beforeEachChild(VisitHandler<T> handler);

    NodeVisitorConfiguration<T> afterEachChild(VisitHandler<T> handler);

    NodeVisitorConfiguration<T> beforeAllChildren(VisitHandler<T> handler);

    NodeVisitorConfiguration<T> afterAllChildren(VisitHandler<T> handler);
}
