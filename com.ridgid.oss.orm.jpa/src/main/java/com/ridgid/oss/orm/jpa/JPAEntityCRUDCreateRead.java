package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.EntityCRUDCreateRead;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionAlreadyExists;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;

import javax.persistence.EntityManager;

/**
 * Base class for a JPA DAO that provides CREATE and READ CRUD operations only
 *
 * @param <ET>  entity type of the entity that the DAO provides persistence methods for
 * @param <PKT> primary key type of the entity type that the DAO provides persistence methods for
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class JPAEntityCRUDCreateRead<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPAEntityCRUDRead<ET, PKT>
        implements
        EntityCRUDCreateRead<ET, PKT> {

    private final JPAEntityCRUDCreateDelegate<ET, PKT> createDelegate;

    public JPAEntityCRUDCreateRead(JPAEntityCRUDDelegate<ET, PKT> baseDelegate) {
        super(baseDelegate);
        this.createDelegate = new JPAEntityCRUDCreateDelegate<>(baseDelegate);
    }

    public JPAEntityCRUDCreateRead(Class<ET> classType,
                                   Class<PKT> pkType) {
        this(new JPAEntityCRUDDelegate<>(classType, pkType));
    }

    public JPAEntityCRUDCreateRead(Class<ET> classType,
                                   Class<PKT> pkType,
                                   String pkName) {
        this(new JPAEntityCRUDDelegate<>(classType, pkType, pkName));
    }

    public JPAEntityCRUDCreateRead(Class<ET> classType,
                                   Class<PKT> pkType,
                                   short loadBatchSize) {
        this(new JPAEntityCRUDDelegate<>(classType, pkType, loadBatchSize));
    }

    public JPAEntityCRUDCreateRead(Class<ET> classType,
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
    }

    /**
     * Adds the given entity to the persistence store (insert/create)
     *
     * @param entity the valid entity to store in the persistence layer that is not already created/inserted by primary key
     * @return the entity one any database or persistence layer modifications applied after successful create/insert
     * @throws EntityCRUDExceptionError         if there is an issue inserting/creating the record (specific "cause" may vary)
     * @throws EntityCRUDExceptionAlreadyExists if and entity one the same primary key of the given entity already exists in the persistent storage
     */
    @Override
    public ET add(ET entity, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError, EntityCRUDExceptionAlreadyExists {
        return createDelegate.add(entity, hierarchy);
    }
}
