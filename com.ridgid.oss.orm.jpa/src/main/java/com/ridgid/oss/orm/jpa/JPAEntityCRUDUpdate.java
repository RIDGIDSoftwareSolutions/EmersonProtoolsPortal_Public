package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDUpdate;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

public class JPAEntityCRUDUpdate<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends JPAEntityCRUD<ET, PKT>
        implements EntityCRUDUpdate<ET, PKT> {

    private final Class<ET> classType;

    protected JPAEntityCRUDUpdate(Class<ET> classType) {
        this.classType = classType;
    }

    @Override
    public ET update(ET entity) {
        if (getEntityManager().find(classType, entity.getPk()) == null)
            throw new javax.persistence.EntityNotFoundException(entity.getPk().toString());
        getEntityManager().merge(entity);
        getEntityManager().flush();
        getEntityManager().refresh(entity);
        return entity;
    }
}
