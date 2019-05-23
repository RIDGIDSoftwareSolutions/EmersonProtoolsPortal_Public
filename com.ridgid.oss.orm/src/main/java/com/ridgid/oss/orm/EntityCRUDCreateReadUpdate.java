package com.ridgid.oss.orm;

/**
 * Indicates the DAO implements the CREATE (add), READ (find), and UPDATE (update) methods
 *
 * @param <ET>  entity type of entity the DAO provides CREATE, READ, and UPDATE CRUD methods for
 * @param <PKT> type of the primary key of the entity type ET
 */
public interface EntityCRUDCreateReadUpdate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUDCreateRead<ET, PKT>, EntityCRUDUpdate<ET, PKT> {
}
