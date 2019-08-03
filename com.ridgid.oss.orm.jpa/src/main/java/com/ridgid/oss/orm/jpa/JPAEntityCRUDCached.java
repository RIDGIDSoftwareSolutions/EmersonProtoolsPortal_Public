package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.cache.Cache;
import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.EntityCRUDCached;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base Class for a DAO for a PrimaryKeyedEntity where the implementation of the DAO uses JPA and the entity is expected to have the needed JPA annotations
 * <p>
 * NOTE: This base class should not normally be inherited from directly. Instead, extend one of the JPAEntityCRUD* base classes that extend this class.
 *
 * @param <ET>  entity type of the entity that the DAO provides persistence methods for
 * @param <PKT> primary key type of the entity type that the DAO provides persistence methods for
 */
@SuppressWarnings({"unused"})
public class JPAEntityCRUDCached<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        //extends JPAEntityCRUD<ET, PKT>
        implements EntityCRUDCached<ET, PKT> {


    @Override
    public HierarchyProcessor<ET> standardHierarchy() {
        return null;
    }

    @Override
    public Cache<PKT, ET> cache() {
        return null;
    }

    @Override
    public <ST extends PrimaryKeyedEntity<SPKT>, SPKT extends Comparable<SPKT>> Optional<Cache<SPKT, ST>> cache(Class<ST> entityClass) {
        return Optional.empty();
    }

    @Override
    public Optional<ET> load(PKT pk) {
        return Optional.empty();
    }

    @Override
    public Stream<ET> loadBatch(List<PKT> pkList) {
        return null;
    }

    @Override
    public short getLoadBatchSize() {
        return 0;
    }

    @Override
    public ET initialize(ET entity, HierarchyProcessor<ET> hierarchy) {
        return null;
    }

    @Override
    public ET detach(ET entity, HierarchyProcessor<ET> hierarchy) {
        return null;
    }
}
