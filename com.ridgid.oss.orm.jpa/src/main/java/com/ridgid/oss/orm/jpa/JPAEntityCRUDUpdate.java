package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDUpdate;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;

import java.util.Optional;

/**
 * Base class for a JPA DAO that provides READ CRUD operations only
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides an UPDATE (update) CRUD method for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
@SuppressWarnings("WeakerAccess")
public class JPAEntityCRUDUpdate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPAEntityCRUD<ET, PKT>
        implements EntityCRUDUpdate<ET, PKT> {

    public JPAEntityCRUDUpdate(Class<ET> classType,
                               Class<PKT> pkType) {
        super(classType, pkType);
    }

    public JPAEntityCRUDUpdate(Class<ET> classType, Class<PKT> pkType, String pkName) {
        super(classType, pkType, pkName);
    }

    public JPAEntityCRUDUpdate(Class<ET> classType, Class<PKT> pkType, short loadBatchSize) {
        super(classType, pkType, loadBatchSize);
    }

    public JPAEntityCRUDUpdate(Class<ET> classType, Class<PKT> pkType, String pkName, short loadBatchSize) {
        super(classType, pkType, pkName, loadBatchSize);
    }

    protected JPAEntityCRUDUpdate(Class<ET> classType) {
        this.JPAEntityCRUDBaseDelegate.classType = classType;
    }

    /**
     * Updates the entity in the persistent storage one the same primary key of given entity so that all the persistent field values in the persistent storage for the entity match the values of the given entity.
     *
     * @param entity entity to update in the persistent storage
     * @return the optional entity one any values that were updated by the persistent storage layer upon successful update of the entity in the persistent storage; if the entity does not currently exist in the persistent storage returns an empty optional.
     * @throws EntityCRUDExceptionError if there is an issue updating the record (specific "cause" may vary)
     */
    @Override
    public Optional<ET> optionalUpdate(ET entity) throws EntityCRUDExceptionError {
        try {
            if (!JPAEntityCRUDDelegate.getEntityManager().contains(entity)) {
                if (JPAEntityCRUDDelegate.getEntityManager().find(JPAEntityCRUDDelegate.classType, entity.getPk()) == null)
                    return Optional.empty();
                JPAEntityCRUDDelegate.getEntityManager().merge(entity);
            }
            JPAEntityCRUDDelegate.getEntityManager().flush();
            JPAEntityCRUDDelegate.getEntityManager().refresh(entity);
            return Optional.of(entity);
        } catch (Exception ex) {
            throw JPAEntityCRUDDelegate.enhanceExceptionWithEntityManagerNullCheck(ex);
        }
    }
}
