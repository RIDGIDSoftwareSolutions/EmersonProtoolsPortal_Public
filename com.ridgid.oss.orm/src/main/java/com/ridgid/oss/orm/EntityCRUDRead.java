package com.ridgid.oss.orm;

import java.util.List;
import java.util.Optional;

/**
 * Indicates the DAO implements the READ (find) methods for the Entity Type ET
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides READ (find) CRUD methods for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
public interface EntityCRUDRead<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUD<ET, PKT> {

    /**
     * Finds and retrieves the entity ET instance (if it exits) from the persistence layer
     *
     * @param pk primary key of the entity ET type to find and retrieve
     * @return entity instance of type ET if the entity exists under the given primary key, pk, in the persistence store
     * @throws EntityCRUDExceptionError    if there is some error retrieving the value
     * @throws EntityCRUDExceptionNotFound if there is no entity ET with the primary key PK in the persistence store
     */
    default ET find(PKT pk) throws EntityCRUDExceptionError, EntityCRUDExceptionNotFound {
        return optionalFind(pk).orElseThrow(EntityCRUDExceptionNotFound::new);
    }

    /**
     * Finds and retrieves the entity ET instance (if it exits) from the persistence layer
     *
     * @param pk primary key of the entity ET type to find and retrieve
     * @return Optional entity instance of type ET if the entity exists under the given primary key, pk, in the persistence store; otherwise, Optional is empty
     * @throws EntityCRUDExceptionError if there is some error retrieving the value beyond it not existing
     */
    Optional<ET> optionalFind(PKT pk) throws EntityCRUDExceptionError;

    /**
     * Finds and retrieves all available entities of type ET in the persistence store between the offset (inclusive, zero-based) up to offset + limit (exclusive)
     *
     * @param offset number of records to skip before beginning to return records
     * @param limit  number of records to retrieve after skipping offset records (or fewer if fewer are available)
     * @return list of entities of type ET that are available in the persistence store ranged by the limit and offset given. If none available in the given range, then returns a 0 length list.
     * @throws EntityCRUDExceptionError if there is an error retrieving from the persistence store
     */
    List<ET> findAll(int offset, int limit) throws EntityCRUDExceptionError;
}
