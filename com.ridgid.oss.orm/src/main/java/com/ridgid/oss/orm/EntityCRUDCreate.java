package com.ridgid.oss.orm;

public interface EntityCRUDCreate<T extends PrimaryKeyedEntity<PKT>,PKT> extends EntityCRUD<T,PKT> {
    T add(T entity);
}
