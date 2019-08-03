package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.EntityCRUDCreateReadUpdateDelete;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionNotFound;

import javax.persistence.EntityManager;

/**
 * Base class for a JPA DAO that provides CREATE, READ, UPDATE, and DELETE CRUD operations only
 *
 * @param <ET>  entity type of the entity that the DAO provides persistence methods for
 * @param <PKT> primary key type of the entity type that the DAO provides persistence methods for
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class JPAEntityCRUDCreateReadUpdateDelete<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPAEntityCRUDCreateReadUpdate<ET, PKT>
        implements EntityCRUDCreateReadUpdateDelete<ET, PKT> {

    private final JPAEntityCRUDDeleteDelegate<ET, PKT> deleteDelegate;

    public JPAEntityCRUDCreateReadUpdateDelete(JPAEntityCRUDDelegate<ET, PKT> baseDelegate) {
        super(baseDelegate);
        this.deleteDelegate = new JPAEntityCRUDDeleteDelegate<>(baseDelegate);
    }

    public JPAEntityCRUDCreateReadUpdateDelete(Class<ET> classType,
                                               Class<PKT> pkType) {
        this(new JPAEntityCRUDDelegate<>(classType, pkType));
    }

    public JPAEntityCRUDCreateReadUpdateDelete(Class<ET> classType,
                                               Class<PKT> pkType,
                                               String pkName) {
        this(new JPAEntityCRUDDelegate<>(classType, pkType, pkName));
    }

    public JPAEntityCRUDCreateReadUpdateDelete(Class<ET> classType,
                                               Class<PKT> pkType,
                                               short loadBatchSize) {
        this(new JPAEntityCRUDDelegate<>(classType, pkType, loadBatchSize));
    }

    public JPAEntityCRUDCreateReadUpdateDelete(Class<ET> classType,
                                               Class<PKT> pkType,
                                               String pkName,
                                               short loadBatchSize) {
        this(new JPAEntityCRUDDelegate<>(classType, pkType, pkName, loadBatchSize));
    }

    /**
     * Sets the JPA entity manager used by the DAO
     *
     * @param entityManager that the DAO should use for all JPA Entity Manager actions
     */
    @Override
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
        deleteDelegate.setEntityManager(entityManager);
    }

    /**
     * Deletes the entity one the given primary key pk from the persistent storage
     *
     * @param pk primary key of the entity to delete from persistent storage
     * @throws EntityCRUDExceptionError    if there is an issue deleting/removing the record (specific "cause" may vary)
     * @throws EntityCRUDExceptionNotFound if there is no entity ET one the primary key PK in the persistence store
     */
    @Override
    public void delete(PKT pk, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError, EntityCRUDExceptionNotFound {
        deleteDelegate.delete(pk, hierarchy);
    }
}
