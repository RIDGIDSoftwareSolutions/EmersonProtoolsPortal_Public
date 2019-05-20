package com.ridgid.oss.orm;

public interface EntityCRUDCreate<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends EntityCRUD<ET, PKT> {
    ET add(ET entity);
}
