package com.ridgid.oss.orm;

/**
 * Thrown when an attempt is made to add an entity to the child collection of a given entity
 * that has a primary key that is supposed to be prefixed by the parent key and is not
 *
 * For example, if Order has an Customer Number and Order number as its Primary Key and
 * it has a collection of Order Lines that is supposed to have Customer Number, Order Number, and Line Number
 * as its Primary Key and the Order Line that is being attempted to add to the collection has a different
 * Customer Number and/or Order Number from the Parent, this exception could/should be thrown.
 */
public class EntityCRUDExceptionSuffixedPrimaryKeyIncompatible extends EntityCRUDException {
    public EntityCRUDExceptionSuffixedPrimaryKeyIncompatible() {
    }

    public EntityCRUDExceptionSuffixedPrimaryKeyIncompatible(String message) {
        super(message);
    }

    public EntityCRUDExceptionSuffixedPrimaryKeyIncompatible(String message, Throwable cause) {
        super(message, cause);
    }
}
