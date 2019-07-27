package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreate;
import com.ridgid.oss.orm.EntityCRUDExceptionAlreadyExists;
import com.ridgid.oss.orm.EntityCRUDExceptionError;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

/**
 * Base class for a JPA DAO that provides CREATE CRUD operations only
 *
 * @param <ET>  entity type of the entity that the DAO provides persistence methods for
 * @param <PKT> primary key type of the entity type that the DAO provides persistence methods for
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class JPAEntityCRUDCreate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPAEntityCRUD<ET, PKT>
        implements EntityCRUDCreate<ET, PKT> {

    @SuppressWarnings("FieldCanBeLocal")
    private final Class<ET> classType;

    protected JPAEntityCRUDCreate(Class<ET> classType) {
        this.classType = classType;
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
        try {
            getEntityManager().persist(entity);
            getEntityManager().flush();
            getEntityManager().refresh(entity);
            return entity;
        } catch (RuntimeException e) {
            throw enhanceWithEntityManagerNullCheck(e);
        }
    }
}
