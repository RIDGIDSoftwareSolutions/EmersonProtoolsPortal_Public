package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDDelete;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

public class JPAEntityCRUDDelete<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends JPAEntityCRUD<ET, PKT>
        implements EntityCRUDDelete<ET, PKT> {

    private final Class<ET> classType;
    private final Class<PKT> pkType;

    protected JPAEntityCRUDDelete(Class<ET> classType, Class<PKT> pkType) {
        this.classType = classType;
        this.pkType = pkType;
    }

    @Override
    public void delete(PKT pk) {
        ET entity = getEntityManager().find(classType, pk);
        if (entity != null) getEntityManager().remove(entity);
    }
}
