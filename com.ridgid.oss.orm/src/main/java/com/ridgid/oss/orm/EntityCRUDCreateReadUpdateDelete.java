package com.ridgid.oss.orm;

/**
 * Indicates the DAO implements the CREATE (add), READ (find), UPDATE (update), and DELETE (delete) methods
 *
 * @param <ET>  entity type of entity the DAO provides CREATE, READ, UPDATE, and DELETE CRUD methods for
 * @param <PKT> type of the primary key of the entity type ET
 */
public interface EntityCRUDCreateReadUpdateDelete<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUDCreateReadUpdate<ET, PKT>, EntityCRUDDelete<ET, PKT> {
}
