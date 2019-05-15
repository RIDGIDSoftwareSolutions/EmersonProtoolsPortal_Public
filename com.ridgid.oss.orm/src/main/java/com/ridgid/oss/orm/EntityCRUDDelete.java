package com.ridgid.oss.orm;

public interface EntityCRUDDelete<T extends PrimaryKeyedEntity<PKT>,PKT> extends EntityCRUD<T,PKT> {
    void delete(PKT pk);
}
