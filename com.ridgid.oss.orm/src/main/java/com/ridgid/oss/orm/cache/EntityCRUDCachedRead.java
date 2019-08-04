package com.ridgid.oss.orm.cache;

import com.ridgid.oss.orm.EntityCRUDCreateRead;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;

/**
 * Indicates the DAO implements the READ (find) methods for the Entity Type ET
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides READ (find) CRUD methods for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
public interface EntityCRUDCachedRead<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends
        EntityCRUDCached<ET, PKT>,
        EntityCRUDCreateRead<ET, PKT> {
}
