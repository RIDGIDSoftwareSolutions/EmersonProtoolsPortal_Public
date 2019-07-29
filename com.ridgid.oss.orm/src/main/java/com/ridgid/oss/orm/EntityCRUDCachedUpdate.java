package com.ridgid.oss.orm;

import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;

/**
 * Indicates the DAO implements the UPDATE (update) method for the Entity Type ET
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides an UPDATE (update) CRUD method for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
public interface EntityCRUDCachedUpdate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends
        EntityCRUDCached<ET, PKT>,
        EntityCRUDUpdate<ET, PKT> {
}
