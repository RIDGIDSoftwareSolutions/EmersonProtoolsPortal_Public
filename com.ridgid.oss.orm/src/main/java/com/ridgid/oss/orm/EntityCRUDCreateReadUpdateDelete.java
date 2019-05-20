package com.ridgid.oss.orm;

public interface EntityCRUDCreateReadUpdateDelete<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends EntityCRUDCreateReadUpdate<ET, PKT>, EntityCRUDDelete<ET, PKT> {
}
