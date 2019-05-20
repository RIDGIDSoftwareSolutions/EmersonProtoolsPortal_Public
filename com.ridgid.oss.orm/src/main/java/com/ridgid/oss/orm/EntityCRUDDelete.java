package com.ridgid.oss.orm;

public interface EntityCRUDDelete<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends EntityCRUD<ET, PKT> {
    void delete(PKT pk);
}
