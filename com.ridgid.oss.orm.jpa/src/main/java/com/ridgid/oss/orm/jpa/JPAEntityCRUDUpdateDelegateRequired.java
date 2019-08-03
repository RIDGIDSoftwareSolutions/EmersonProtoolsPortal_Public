package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.EntityCRUDUpdate;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;

import java.util.Optional;

@SuppressWarnings("unused")
public interface JPAEntityCRUDUpdateDelegateRequired<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUDUpdate<ET, PKT> {

    @Override
    Optional<ET> optionalUpdate(ET entity, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError;
}
