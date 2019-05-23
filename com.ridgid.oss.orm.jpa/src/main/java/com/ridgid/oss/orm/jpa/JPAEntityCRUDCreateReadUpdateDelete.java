package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreateReadUpdateDelete;
import com.ridgid.oss.orm.EntityCRUDExceptionError;
import com.ridgid.oss.orm.EntityCRUDExceptionNotFound;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

/**
 * Base class for a JPA DAO that provides CREATE, READ, UPDATE, and DELETE CRUD operations only
 *
 * @param <ET>  entity type of the entity that the DAO provides persistence methods for
 * @param <PKT> primary key type of the entity type that the DAO provides persistence methods for
 */
public abstract class JPAEntityCRUDCreateReadUpdateDelete<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPAEntityCRUDCreateReadUpdate<ET, PKT>
        implements EntityCRUDCreateReadUpdateDelete<ET, PKT> {

    private final JPAEntityCRUDDelete<ET, PKT> deleteBase;

    protected JPAEntityCRUDCreateReadUpdateDelete(Class<ET> classType, Class<PKT> pkType) {
        super(classType, pkType);
        deleteBase = new JPAEntityCRUDDelete<>(classType,pkType);
    }

    /**
     * Sets the JPA entity manager used by the DAO
     *
     * @param entityManager that the DAO should use for all JPA Entity Manager actions
     */
    @Override
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
        deleteBase.setEntityManager(entityManager);
    }

    /**
     * Deletes the entity with the given primary key pk from the persistent storage
     *
     * @param pk primary key of the entity to delete from persistent storage
     * @throws EntityCRUDExceptionError    if there is an issue deleting/removing the record (specific "cause" may vary)
     * @throws EntityCRUDExceptionNotFound if there is no entity ET with the primary key PK in the persistence store
     */
    @Override
    public void delete(PKT pk) throws EntityCRUDExceptionError, EntityCRUDExceptionNotFound {
        deleteBase.delete(pk);
    }
}
