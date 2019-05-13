package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreateRead;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

public class JPAEntityCRUDCreateRead<T extends PrimaryKeyedEntity<PKT>, PKT>
        extends JPAEntityCRUDRead<T, PKT>
        implements EntityCRUDCreateRead<T, PKT> {

    private final JPAEntityCRUDCreate<T,PKT> createBase;

    public JPAEntityCRUDCreateRead(Class<T> classType, Class<PKT> pkType) {
        super(classType, pkType);
        createBase = new JPAEntityCRUDCreate<>(classType);
    }

    @Override
    public void setEm(EntityManager em) {
        super.setEm(em);
        createBase.setEm(em);
    }

    @Override
    public T add(T entity) {
        return createBase.add(entity);
    }
}
