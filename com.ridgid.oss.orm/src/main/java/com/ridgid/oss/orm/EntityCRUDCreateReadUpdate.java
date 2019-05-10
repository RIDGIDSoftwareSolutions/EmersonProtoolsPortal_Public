package com.ridgid.oss.orm;

public interface EntityCRUDCreateReadUpdate<T extends PrimaryKeyedEntity<PKT>,PKT> extends EntityCRUDCreateRead<T,PKT>, EntityCRUDUpdate<T,PKT> {
}
