package com.ridgid.oss.orm;

/**
 * Indicates the DAO implements the CREATE (add) method for the Entity Type ET
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides a CREATE (add) CRUD method for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
public interface EntityCRUDDelete<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends EntityCRUD<ET, PKT> {

    /**
     * Deletes the entity with the given primary key pk from the persistent storage
     *
     * @param pk primary key of the entity to delete from persistent storage
     * @throws EntityCRUDExceptionError    if there is an issue deleting/removing the record (specific "cause" may vary)
     * @throws EntityCRUDExceptionNotFound if there is no entity ET with the primary key PK in the persistence store
     */
    void delete(PKT pk) throws EntityCRUDExceptionError, EntityCRUDExceptionNotFound;

    /**
     * Deletes the entity with the given primary key pk from the persistent storage. If the entity already does not exist with the given PK, it returns normally as if it successfully deleted.
     *
     * @param pk primary key of the entity to delete from persistent storage
     * @throws EntityCRUDExceptionError if there is an issue deleting/removing the record (specific "cause" may vary)
     */
    default void optionalDelete(PKT pk) throws EntityCRUDExceptionError {
        try {
            delete(pk);
        } catch (EntityCRUDExceptionNotFound ignore) {
        }
    }
}
