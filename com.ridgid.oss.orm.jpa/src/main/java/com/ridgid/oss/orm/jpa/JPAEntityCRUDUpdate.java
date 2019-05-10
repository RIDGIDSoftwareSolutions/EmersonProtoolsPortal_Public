package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDUpdate;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

public class JPAEntityCRUDUpdate<T extends PrimaryKeyedEntity<PKT>, PKT> implements EntityCRUDUpdate<T,PKT> {

    private final Class<T> classType;

    private EntityManager em;

    public JPAEntityCRUDUpdate(Class<T> classType) {
        this.classType = classType;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    protected final EntityManager getEm() {
        return em;
    }

    @Override
    public T update(T entity) {
        if (em.find(classType, entity.getPK()) == null)
            throw new javax.persistence.EntityNotFoundException(entity.getPK().toString());
        em.merge(entity);
        em.flush();
        em.refresh(entity);
        return entity;
    }
}
