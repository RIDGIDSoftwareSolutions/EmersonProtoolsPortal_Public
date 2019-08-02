package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.Hierarchy;
import com.ridgid.oss.orm.EntityCRUDRead;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;

import java.util.List;
import java.util.Optional;

public interface JPAEntityCRUDReadDelegateRequired<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUDRead<ET, PKT> {

    /**
     * Finds and retrieves the entity ET instance (if it exits) from the persistence layer
     *
     * @param pk primary key of the entity ET type to find and retrieve
     * @return Optional entity instance of type ET if the entity exists under the given primary key, pk, in the persistence store; otherwise, Optional is empty
     * @throws EntityCRUDExceptionError if there is some error retrieving the value beyond it not existing
     */
    Optional<ET> optionalFind(PKT pk, Hierarchy<ET> hierarchy) throws EntityCRUDExceptionError;

    /**
     * Finds and retrieves all available entities of type ET in the persistence store between the offset (inclusive, zero-based) up to offset + limit (exclusive)
     *
     * @param offset number of records to skip before beginning to return records
     * @param limit  number of records to retrieve after skipping offset records (or fewer if fewer are available)
     * @return list of entities of type ET that are available in the persistence store ranged by the limit and offset given. If none available in the given range, then returns a 0 length list.
     * @throws EntityCRUDExceptionError if there is an error retrieving from the persistence store
     */
    List<ET> findAll(int offset, int limit, Hierarchy<ET> hierarchy) throws EntityCRUDExceptionError;
}
