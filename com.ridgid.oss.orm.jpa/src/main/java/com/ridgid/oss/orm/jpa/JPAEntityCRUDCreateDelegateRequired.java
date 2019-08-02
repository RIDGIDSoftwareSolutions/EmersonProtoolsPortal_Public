package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.Hierarchy;
import com.ridgid.oss.orm.EntityCRUDCreate;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionAlreadyExists;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;

@SuppressWarnings("unused")
public interface JPAEntityCRUDCreateDelegateRequired<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUDCreate<ET, PKT> {

    @Override
    ET add(ET entity, Hierarchy<ET> hierarchy) throws EntityCRUDExceptionError, EntityCRUDExceptionAlreadyExists;
}
