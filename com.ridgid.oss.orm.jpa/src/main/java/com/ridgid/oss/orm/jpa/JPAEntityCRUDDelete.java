package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDDelete;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

public class JPAEntityCRUDDelete<T extends PrimaryKeyedEntity<PKT>, PKT> implements EntityCRUDDelete<T,PKT> {

    private final Class<T> classType;
    private final Class<PKT> pkType;

    private EntityManager em;

    public JPAEntityCRUDDelete(Class<T> classType, Class<PKT> pkType) {
        this.classType = classType;
        this.pkType = pkType;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    protected final EntityManager getEm() {
        return em;
    }

    @Override
    public void delete(PKT pk) {
        T entity = em.find(classType, pk);
        if (entity != null) em.remove(entity);
    }
}
