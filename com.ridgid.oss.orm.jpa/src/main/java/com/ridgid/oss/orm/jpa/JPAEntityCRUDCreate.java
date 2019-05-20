package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDCreate;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

public class JPAEntityCRUDCreate<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends JPAEntityCRUD<ET, PKT>
        implements EntityCRUDCreate<ET, PKT> {

    private final Class<ET> classType;

    protected JPAEntityCRUDCreate(Class<ET> classType) {
        this.classType = classType;
    }

    @Override
    public ET add(ET entity) {
        getEntityManager().persist(entity);
        getEntityManager().flush();
        getEntityManager().refresh(entity);
        return entity;
    }
}
