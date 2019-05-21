package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUD;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

/**
 * Base Class for a DAO for a PrimaryKeyedEntity where the implementation of the DAO uses JPA and the entity is expected to have the needed JPA annotations
 * <p>
 * NOTE: This base class should not normally be inherited from directly. Instead, extend one of the JPAEntityCRUD* base classes that extend this class.
 *
 * @param <ET>  entity type of the entity that the DAO provides persistence methods for
 * @param <PKT> primary key type of the entity type that the DAO provides persistence methods for
 */
public abstract class JPAEntityCRUD<ET extends PrimaryKeyedEntity<PKT>, PKT>
        implements EntityCRUD<ET, PKT> {

    private EntityManager entityManager;

    /**
     * Sets the JPA entity manager used by the DAO
     *
     * @param entityManager that the DAO should use for all JPA Entity Manager actions
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Gets the JPA entity manager set for the DAO
     *
     * @return JPA EntityManager or null if not set
     */
    public final EntityManager getEntityManager() {
        return entityManager;
    }
}
