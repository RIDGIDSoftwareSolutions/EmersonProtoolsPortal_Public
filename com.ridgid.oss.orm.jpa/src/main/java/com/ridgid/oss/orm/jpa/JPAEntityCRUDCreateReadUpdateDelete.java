package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreateReadUpdateDelete;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

public class JPAEntityCRUDCreateReadUpdateDelete<T extends PrimaryKeyedEntity<PKT>, PKT>
        extends JPAEntityCRUDCreateReadUpdate<T, PKT>
        implements EntityCRUDCreateReadUpdateDelete<T, PKT> {

    private final JPAEntityCRUDDelete<T,PKT> deleteBase;

    public JPAEntityCRUDCreateReadUpdateDelete(Class<T> classType, Class<PKT> pkType) {
        super(classType, pkType);
        deleteBase = new JPAEntityCRUDDelete<>(classType,pkType);
    }

    @Override
    public void setEm(EntityManager em) {
        super.setEm(em);
        deleteBase.setEm(em);
    }

    @Override
    public void delete(PKT pk) {
        deleteBase.delete(pk);
    }
}
