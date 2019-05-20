package com.ridgid.oss.orm;

/**
 * Indicates the DAO implements both  the CREATE (add) and READ (find) methods
 *
 * @param <ET>  entity type of entity the DAO provides CREATE and READ CRUD methods for
 * @param <PKT> type of the primary key of the entity type ET
 */
public interface EntityCRUDCreateRead<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends EntityCRUDCreate<ET, PKT>, EntityCRUDRead<ET, PKT> {
}
