package com.ridgid.oss.orm.cache;

import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;

/**
 * Indicates the DAO implements the CREATE (add), READ (find), UPDATE (update), and DELETE (delete) methods
 *
 * @param <ET>  entity type of entity the DAO provides CREATE, READ, UPDATE, and DELETE CRUD methods for
 * @param <PKT> type of the primary key of the entity type ET
 */
public interface EntityCRUDCachedCreateReadUpdateDelete<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends
        EntityCRUDCachedCreateReadUpdate<ET, PKT>,
        EntityCRUDCachedDelete<ET, PKT> {
}
