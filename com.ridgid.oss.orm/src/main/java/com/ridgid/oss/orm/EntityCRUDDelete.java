package com.ridgid.oss.orm;

public interface EntityCRUDDelete<T extends PrimaryKeyedEntity<PKT>,PKT> {
    void delete(PKT pk);
}
