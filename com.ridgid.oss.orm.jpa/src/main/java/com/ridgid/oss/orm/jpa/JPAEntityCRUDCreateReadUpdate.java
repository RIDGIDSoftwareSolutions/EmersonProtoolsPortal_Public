package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreateReadUpdate;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

public abstract class JPAEntityCRUDCreateReadUpdate<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends JPAEntityCRUDCreateRead<ET, PKT>
        implements EntityCRUDCreateReadUpdate<ET, PKT> {

    private final JPAEntityCRUDUpdate<ET, PKT> updateBase;

    protected JPAEntityCRUDCreateReadUpdate(Class<ET> classType,
                                         Class<PKT> pkType) {
        super(classType, pkType);
        updateBase = new JPAEntityCRUDUpdate<>(classType);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
        updateBase.setEntityManager(em);
    }

    @Override
    public ET update(ET entity) {
        return updateBase.update(entity);
    }
}
