package com.ridgid.oss.orm.cache;

import com.ridgid.oss.orm.EntityCRUDDelete;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;

/**
 * Indicates the DAO implements the CREATE (add) method for the Entity Type ET
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides a CREATE (add) CRUD method for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
public interface EntityCRUDCachedDelete<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends
        EntityCRUDCached<ET, PKT>,
        EntityCRUDDelete<ET, PKT> {
}
