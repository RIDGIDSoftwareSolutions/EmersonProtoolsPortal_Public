package com.ridgid.oss.orm;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionNotFound;

import java.util.Optional;

/**
 * Indicates the DAO implements the UPDATE (update) method for the Entity Type ET
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides an UPDATE (update) CRUD method for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
public interface EntityCRUDUpdate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUD<ET, PKT> {

    /**
     * Updates the entity in the persistent storage one the same primary key of given entity so that all the persistent field values in the persistent storage for the entity match the values of the given entity.
     *
     * @param entity entity to update in the persistent storage
     * @return the entity one any values that were updated by the persistent storage layer upon successful update of the entity in the persistent storage
     * @throws EntityCRUDExceptionError    if there is an issue updating the record (specific "cause" may vary)
     * @throws EntityCRUDExceptionNotFound if there is no entity ET one the primary key matching the given entity in the persistence store
     */
    default ET update(ET entity) throws EntityCRUDExceptionError, EntityCRUDExceptionNotFound {
        return optionalUpdate(entity).orElseThrow(EntityCRUDExceptionNotFound::new);
    }

    default ET update(ET entity, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError, EntityCRUDExceptionNotFound {
        return optionalUpdate(entity, hierarchy).orElseThrow(EntityCRUDExceptionNotFound::new);
    }

    /**
     * Updates the entity in the persistent storage one the same primary key of given entity so that all the persistent field values in the persistent storage for the entity match the values of the given entity.
     *
     * @param entity entity to update in the persistent storage
     * @return the optional entity one any values that were updated by the persistent storage layer upon successful update of the entity in the persistent storage; if the entity does not currently exist in the persistent storage returns an empty optional.
     * @throws EntityCRUDExceptionError if there is an issue updating the record (specific "cause" may vary)
     */
    default Optional<ET> optionalUpdate(ET entity) throws EntityCRUDExceptionError {
        return optionalUpdate(entity, null);
    }

    Optional<ET> optionalUpdate(ET entity, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError;
}
