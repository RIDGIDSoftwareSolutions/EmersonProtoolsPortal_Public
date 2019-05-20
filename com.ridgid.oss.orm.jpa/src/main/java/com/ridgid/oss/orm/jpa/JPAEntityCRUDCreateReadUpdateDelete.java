package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreateReadUpdateDelete;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

public abstract class JPAEntityCRUDCreateReadUpdateDelete<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends JPAEntityCRUDCreateReadUpdate<ET, PKT>
        implements EntityCRUDCreateReadUpdateDelete<ET, PKT> {

    private final JPAEntityCRUDDelete<ET, PKT> deleteBase;

    protected JPAEntityCRUDCreateReadUpdateDelete(Class<ET> classType, Class<PKT> pkType) {
        super(classType, pkType);
        deleteBase = new JPAEntityCRUDDelete<>(classType,pkType);
    }

    @Override
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
        deleteBase.setEntityManager(entityManager);
    }

    @Override
    public void delete(PKT pk) {
        deleteBase.delete(pk);
    }
}
