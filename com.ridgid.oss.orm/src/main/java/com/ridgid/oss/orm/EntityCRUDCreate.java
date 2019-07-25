package com.ridgid.oss.orm;

/**
 * Indicates the DAO implements the CREATE (add) method for the Entity Type ET
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides a CREATE (add) CRUD method for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
public interface EntityCRUDCreate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUD<ET, PKT> {

    /**
     * Adds the given entity to the persistence store (insert/create)
     *
     * @param entity the valid entity to store in the persistence layer that is not already created/inserted by primary key
     * @return the entity one any database or persistence layer modifications applied after successful create/insert
     * @throws EntityCRUDExceptionError         if there is an issue inserting/creating the record (specific "cause" may vary)
     * @throws EntityCRUDExceptionAlreadyExists if and entity one the same primary key of the given entity already exists in the persistent storage
     */
    ET add(ET entity) throws EntityCRUDExceptionError, EntityCRUDExceptionAlreadyExists;

}
