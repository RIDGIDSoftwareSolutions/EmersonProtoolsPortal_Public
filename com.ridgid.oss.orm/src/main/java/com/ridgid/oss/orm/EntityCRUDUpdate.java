package com.ridgid.oss.orm;

public interface EntityCRUDUpdate<T extends PrimaryKeyedEntity<PKT>,PKT> {
    T update(T entity);
}
