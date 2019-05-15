package com.ridgid.oss.orm;

public interface EntityCRUDUpdate<T extends PrimaryKeyedEntity<PKT>,PKT> extends EntityCRUD<T,PKT> {
    T update(T entity);
}
