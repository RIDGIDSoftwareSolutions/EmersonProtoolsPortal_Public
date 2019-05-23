package com.ridgid.oss.orm;

/**
 * Provides marker interface to indicate a DAO that is a valid EntityCRUD DAO for Entity Type ET where ET is a PrimaryKeyedEntity with Primary Key Type PKT
 *
 * @param <ET>  Entity Type of Entity that the DAO is providing CRUD methods for
 * @param <PKT> Type of the Primary Key of the Entity Type
 */
public interface EntityCRUD<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>> {
}
