package com.ridgid.oss.orm;

public interface EntityCRUDCreateRead<T extends PrimaryKeyedEntity<PKT>,PKT> extends EntityCRUDCreate<T,PKT>, EntityCRUDRead<T,PKT> {
}
