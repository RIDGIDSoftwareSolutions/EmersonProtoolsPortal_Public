package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreateReadUpdate;
import com.ridgid.oss.orm.EntityCRUDExceptionError;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;
import java.util.Optional;

/**
 * Base class for a JPA DAO that provides CREATE, READ, and UPDATE CRUD operations only
 *
 * @param <ET>  entity type of the entity that the DAO provides persistence methods for
 * @param <PKT> primary key type of the entity type that the DAO provides persistence methods for
 */
@SuppressWarnings("WeakerAccess")
public abstract class JPAEntityCRUDCreateReadUpdate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPAEntityCRUDCreateRead<ET, PKT>
        implements EntityCRUDCreateReadUpdate<ET, PKT> {

    private final JPAEntityCRUDUpdate<ET, PKT> updateBase;

    protected JPAEntityCRUDCreateReadUpdate(Class<ET> classType,
                                            Class<PKT> pkType) {
        super(classType, pkType);
        updateBase = new JPAEntityCRUDUpdate<>(classType);
    }

    /**
     * Sets the JPA entity manager used by the DAO
     *
     * @param entityManager that the DAO should use for all JPA Entity Manager actions
     */
    @Override
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
        updateBase.setEntityManager(entityManager);
    }

    /**
     * Updates the entity in the persistent storage with the same primary key of given entity so that all the persistent field values in the persistent storage for the entity match the values of the given entity.
     *
     * @param entity entity to update in the persistent storage
     * @return the optional entity with any values that were updated by the persistent storage layer upon successful update of the entity in the persistent storage; if the entity does not currently exist in the persistent storage returns an empty optional.
     * @throws EntityCRUDExceptionError if there is an issue updating the record (specific "cause" may vary)
     */
    @Override
    public Optional<ET> optionalUpdate(ET entity) throws EntityCRUDExceptionError {
        return updateBase.optionalUpdate(entity);
    }
}
