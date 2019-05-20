package com.ridgid.oss.orm;

public interface EntityCRUDCreateRead<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends EntityCRUDCreate<ET, PKT>, EntityCRUDRead<ET, PKT> {
}
