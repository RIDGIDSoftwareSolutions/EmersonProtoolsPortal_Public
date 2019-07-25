package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreateRead;
import com.ridgid.oss.orm.EntityCRUDExceptionAlreadyExists;
import com.ridgid.oss.orm.EntityCRUDExceptionError;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

/**
 * Base class for a JPA DAO that provides CREATE and READ CRUD operations only
 *
 * @param <ET>  entity type of the entity that the DAO provides persistence methods for
 * @param <PKT> primary key type of the entity type that the DAO provides persistence methods for
 */
@SuppressWarnings("WeakerAccess")
public abstract class JPAEntityCRUDCreateRead<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPAEntityCRUDRead<ET, PKT>
        implements EntityCRUDCreateRead<ET, PKT> {

    private final JPAEntityCRUDCreate<ET, PKT> createBase;

    protected JPAEntityCRUDCreateRead(Class<ET> classType, Class<PKT> pkType) {
        super(classType, pkType);
        createBase = new JPAEntityCRUDCreate<>(classType);
    }

    /**
     * Sets the JPA entity manager used by the DAO
     *
     * @param entityManager that the DAO should use for all JPA Entity Manager actions
     */
    @Override
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
        createBase.setEntityManager(entityManager);
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
    public ET add(ET entity) throws EntityCRUDExceptionError, EntityCRUDExceptionAlreadyExists {
        return createBase.add(entity);
    }
}
