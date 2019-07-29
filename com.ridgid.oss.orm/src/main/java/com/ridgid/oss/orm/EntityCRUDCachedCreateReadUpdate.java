package com.ridgid.oss.orm;

import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;

/**
 * Indicates the DAO implements the CREATE (add), READ (find), and UPDATE (update) methods
 *
 * @param <ET>  entity type of entity the DAO provides CREATE, READ, and UPDATE CRUD methods for
 * @param <PKT> type of the primary key of the entity type ET
 */
public interface EntityCRUDCachedCreateReadUpdate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends
        EntityCRUDCachedCreateRead<ET, PKT>,
        EntityCRUDCachedUpdate<ET, PKT> {
}
