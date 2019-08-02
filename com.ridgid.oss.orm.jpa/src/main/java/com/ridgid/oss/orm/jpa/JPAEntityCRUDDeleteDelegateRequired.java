package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.Hierarchy;
import com.ridgid.oss.orm.EntityCRUDDelete;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionNotFound;

@SuppressWarnings("unused")
public interface JPAEntityCRUDDeleteDelegateRequired<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUDDelete<ET, PKT> {

    @Override
    void delete(PKT pk, Hierarchy<ET> hierarchy) throws EntityCRUDExceptionError, EntityCRUDExceptionNotFound;

}
