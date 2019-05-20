package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUD;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

public abstract class JPAEntityCRUD<ET extends PrimaryKeyedEntity<PKT>, PKT>
        implements EntityCRUD<ET, PKT> {

    private EntityManager entityManager;

    protected void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected final EntityManager getEntityManager() {
        return entityManager;
    }
}
