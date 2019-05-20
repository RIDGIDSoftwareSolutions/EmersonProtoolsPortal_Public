package com.ridgid.oss.orm;

public interface EntityCRUDUpdate<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends EntityCRUD<ET, PKT> {
    ET update(ET entity);
}
