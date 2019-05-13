package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreate;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

public class JPAEntityCRUDCreate<T extends PrimaryKeyedEntity<PKT>,PKT> implements EntityCRUDCreate<T,PKT> {

    private final Class<T> classType;

    private EntityManager em;

    public JPAEntityCRUDCreate(Class<T> classType) {
        this.classType = classType;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    protected final EntityManager getEm() {
        return em;
    }

    @Override
    public T add(T entity) {
        em.persist(entity);
        em.flush();
        em.refresh(entity);
        return entity;
    }
}
