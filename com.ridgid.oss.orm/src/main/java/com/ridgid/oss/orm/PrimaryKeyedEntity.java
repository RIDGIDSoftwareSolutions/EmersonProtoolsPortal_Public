package com.ridgid.oss.orm;

import java.io.Serializable;

/**
 * Indicates an entity that has a Primary Key of type PKT with getter for the primary key
 * <p>
 * NOTE: The type PKT must adhere to the following contract:
 * - Implements Serializable interface
 * - Has a no argument constructor
 * - Has a constructor with all of the fields of the primary key, in order, as a parameter -OR- is a primitive wrapper type or String type
 * - PK fields are private
 * - Public "getter" for each of the PK fields
 * - No "setters" for any of the PK fields
 * - Overrides equals and hashcode based on the values of all the fields of the primary key correctly according to the contracts for equals and hashcode
 * - Overrides toString to produce a human-readable representation of all of the values of the primary key
 *
 * @param <PKT> type of the Primary Key of the entity
 */
public interface PrimaryKeyedEntity<PKT> extends Serializable {

    /**
     * Gets the primary key
     *
     * @return primarky key of type PKT of the entity
     */
    PKT getPk();
}
