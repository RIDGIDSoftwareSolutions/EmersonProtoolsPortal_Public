package com.ridgid.oss.orm;

public interface EntityCRUDCreate<T extends PrimaryKeyedEntity<PKT>,PKT> {
    T Add(T entity);
}
