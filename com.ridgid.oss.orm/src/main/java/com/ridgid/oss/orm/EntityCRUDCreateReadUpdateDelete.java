package com.ridgid.oss.orm;

public interface EntityCRUDCreateReadUpdateDelete<T extends PrimaryKeyedEntity<PKT>,PKT> extends EntityCRUDCreateReadUpdate<T,PKT>, EntityCRUDDelete<T,PKT> {
}
