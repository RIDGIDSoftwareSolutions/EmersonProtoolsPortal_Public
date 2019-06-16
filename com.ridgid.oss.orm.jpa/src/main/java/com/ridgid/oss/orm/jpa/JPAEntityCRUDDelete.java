package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDDelete;
import com.ridgid.oss.orm.EntityCRUDExceptionError;
import com.ridgid.oss.orm.EntityCRUDExceptionNotFound;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

/**
 * Base class for a JPA DAO that provides DELETE CRUD operations only
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides a CREATE (add) CRUD method for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
@SuppressWarnings("WeakerAccess")
public class JPAEntityCRUDDelete<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPAEntityCRUD<ET, PKT>
        implements EntityCRUDDelete<ET, PKT> {

    private final Class<ET> classType;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final Class<PKT> pkType;

    protected JPAEntityCRUDDelete(Class<ET> classType, Class<PKT> pkType) {
        this.classType = classType;
        this.pkType = pkType;
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
        try {
            ET entity = getEntityManager().find(classType, pk);
            if (entity == null) throw new EntityCRUDExceptionNotFound();
            getEntityManager().remove(entity);
        } catch (EntityCRUDExceptionNotFound ex) {
            throw ex;
        } catch (Exception ex) {
            throw new EntityCRUDExceptionError(ex);
        }
    }
}
