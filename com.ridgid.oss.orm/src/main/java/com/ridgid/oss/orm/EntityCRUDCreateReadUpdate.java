package com.ridgid.oss.orm;

public interface EntityCRUDCreateReadUpdate<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends EntityCRUDCreateRead<ET, PKT>, EntityCRUDUpdate<ET, PKT> {
}
