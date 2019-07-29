package com.ridgid.oss.orm.entity;

import com.ridgid.oss.common.helper.CopyableModel;

import java.io.Serializable;

/**
 * Indicates an entity that has a Natural Key of type NKT one getter for the primary key
 * <p>
 * NOTE: The type NKT must adhere to the following contract:
 * - Implements Serializable interface
 * - Has a no argument constructor
 * - Has a copy constructor
 * - NK fields are private
 * - Public "getter" for each of the NK fields
 * - No "setters" for any of the NK fields
 * - Overrides equals and hashcode based on the values of all the fields of the natual key correctly according to the contracts for equals and hashcode
 * - Overrides toString to produce a human-readable representation of all of the values of the natural key
 *
 * @param <NKT> type of the Natural Key of the entity
 * @param <PKT> type of the Primary Key of the entity
 */
@SuppressWarnings("unused")
public interface NaturalKeyedEntity<NKT extends ComparableKey<NKT>, PKT extends Comparable<PKT>>
        extends
        PrimaryKeyedEntity<PKT>,
        Serializable,
        CopyableModel {

    /**
     * Gets the natural key
     *
     * @return natural key of type NKT of the entity
     */
    NKT getNk();
}
