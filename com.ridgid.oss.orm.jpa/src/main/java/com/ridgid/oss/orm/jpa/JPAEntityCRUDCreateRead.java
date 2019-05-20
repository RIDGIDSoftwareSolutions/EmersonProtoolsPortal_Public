package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreateRead;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;

public abstract class JPAEntityCRUDCreateRead<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends JPAEntityCRUDRead<ET, PKT>
        implements EntityCRUDCreateRead<ET, PKT> {

    private final JPAEntityCRUDCreate<ET, PKT> createBase;

    protected JPAEntityCRUDCreateRead(Class<ET> classType, Class<PKT> pkType) {
        super(classType, pkType);
        createBase = new JPAEntityCRUDCreate<>(classType);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
        createBase.setEntityManager(em);
    }

    @Override
    public ET add(ET entity) {
        return createBase.add(entity);
    }
}
