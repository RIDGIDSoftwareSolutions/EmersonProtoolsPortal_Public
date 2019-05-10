package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreateReadUpdate;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

public class JPAEntityCRUDCreateReadUpdate<T extends PrimaryKeyedEntity<PKT>,PKT>
        extends JPAEntityCRUDCreateRead<T,PKT>
        implements EntityCRUDCreateReadUpdate<T, PKT> {

    private final JPAEntityCRUDUpdate<T,PKT> updateBase;

    public JPAEntityCRUDCreateReadUpdate(Class<T> classType,
                                         Class<PKT> pkType) {
        super(classType, pkType);
        updateBase = new JPAEntityCRUDUpdate<>(classType);
    }

    @Override
    public void setEm(EntityManager em) {
        super.setEm(em);
        updateBase.setEm(em);
    }

    @Override
    public T update(T entity) {
        return updateBase.update(entity);
    }
}
