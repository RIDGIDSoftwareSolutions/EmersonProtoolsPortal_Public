package com.ridgid.oss.orm.cache;

import com.ridgid.oss.common.cache.Cache;
import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.EntityCRUD;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface for a DAO that is a valid EntityCRUD DAO for Entity Type ET where ET is a PrimaryKeyedEntity one Primary Key Type PKT
 * that should maintain an application-level Entity Hierarchy Cache On-Demand or automatically
 *
 * @param <ET>  Entity Type of Entity that the DAO is providing CRUD methods for
 * @param <PKT> Type of the Primary Key of the Entity Type
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public interface EntityCRUDCached<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUD<ET, PKT> {

    /**
     * Load and initialize Lazily-Loaded fields and dependencies of the given entity, then, cache the entity graph
     * from the Persistence Context and return it
     *
     * @param entity    to load lazy loaded fields and lazy loaded dependencies for
     * @param hierarchy hierarchy with the given entity as the root to load
     * @return detached entity graphy rooted at the given entity with all lazy-loaded fields and dependencies named in the graph initalized
     */
    default ET initializeDetachAndCache(ET entity,
                                        HierarchyProcessor<ET> hierarchy) {
        return cache().put
                (
                        entity.getPk(),
                        initializeAndDetach
                                (
                                        entity,
                                        hierarchy
                                )
                );
    }

    default ET initializeDetachAndCacheStandardHierarchy(ET entity) {
        return initializeDetachAndCache
                (
                        entity,
                        standardHierarchy()
                );
    }

    default Stream<ET> initializeDetachAndCache(Stream<ET> entityStream,
                                                HierarchyProcessor<ET> hierarchy) {
        return initializeAndDetach
                (
                        entityStream,
                        hierarchy
                )
                .stream()
                .map(e -> cache().put(e.getPk(), e));
    }

    default Stream<ET> initializeDetachAndCacheStandardHierarchy(Stream<ET> entityStream) {
        return initializeDetachAndCache
                (
                        entityStream,
                        standardHierarchy()
                );
    }

    default Stream<ET> loadInitializeAndDetachThroughCache(Stream<PKT> pktStream,
                                                           HierarchyProcessor<ET> hierarchy) {
        return loadInitializeAndDetach
                (
                        pktStream,
                        hierarchy
                )
                .stream()
                .map(e -> cache().put(e.getPk(), e));
    }

    default Stream<ET> loadInitializeAndDetachThroughCacheStandardHierarchy(Stream<PKT> pkStream) {
        return loadInitializeAndDetachThroughCache(pkStream, standardHierarchy());
    }

    HierarchyProcessor<ET> standardHierarchy();

    Cache<PKT, ET> cache();

    <ST extends PrimaryKeyedEntity<SPKT>, SPKT extends Comparable<SPKT>>
    Optional<Cache<SPKT, ST>> cache(Class<ST> entityClass);
}
