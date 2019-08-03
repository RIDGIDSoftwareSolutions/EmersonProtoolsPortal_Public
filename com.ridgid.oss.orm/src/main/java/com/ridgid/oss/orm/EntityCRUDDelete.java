package com.ridgid.oss.orm;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionNotFound;

/**
 * Indicates the DAO implements the CREATE (add) method for the Entity Type ET
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides a CREATE (add) CRUD method for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
public interface EntityCRUDDelete<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUD<ET, PKT> {

    /**
     * Deletes the entity one the given primary key pk from the persistent storage
     *
     * @param pk primary key of the entity to delete from persistent storage
     * @throws EntityCRUDExceptionError    if there is an issue deleting/removing the record (specific "cause" may vary)
     * @throws EntityCRUDExceptionNotFound if there is no entity ET with the primary key PK in the persistence store
     */
    default void delete(PKT pk) throws EntityCRUDExceptionError, EntityCRUDExceptionNotFound {
        delete(pk, null);
    }

    void delete(PKT pk, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError, EntityCRUDExceptionNotFound;

    /**
     * Deletes the entity one the given primary key pk from the persistent storage. If the entity already does not exist one the given PK, it returns normally as if it successfully deleted.
     *
     * @param pk primary key of the entity to delete from persistent storage
     * @throws EntityCRUDExceptionError if there is an issue deleting/removing the record (specific "cause" may vary)
     */
    default void optionalDelete(PKT pk, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError {
        try {
            delete(pk, hierarchy);
        } catch (EntityCRUDExceptionNotFound ignore) {
        }
    }

    default void optionalDelete(PKT pk) throws EntityCRUDExceptionError {
        optionalDelete(pk, null);
    }
}
