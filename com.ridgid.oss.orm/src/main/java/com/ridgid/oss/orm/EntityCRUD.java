package com.ridgid.oss.orm;

import com.ridgid.oss.common.hierarchy.Hierarchy;

/**
 * Provides marker interface to indicate a DAO that is a valid EntityCRUD DAO for Entity Type ET where ET is a PrimaryKeyedEntity one Primary Key Type PKT
 *
 * @param <ET>  Entity Type of Entity that the DAO is providing CRUD methods for
 * @param <PKT> Type of the Primary Key of the Entity Type
 */
@SuppressWarnings("unused")
public interface EntityCRUD<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>> {

    /**
     * Load and initialize Lazily-Loaded fields and dependencies of the given entity, then, detach the enity graph
     * from the Persistence Context and return it
     *
     * @param entity          to load lazy loaded fields and lazy loaded dependencies for
     * @param hierarchyToLoad hierarchy with the given entity as the root to load
     * @return detached entity graphy rooted at the given entity with all lazy-loaded fields and dependencies named in the graph initalized
     */
    ET initializeAndDetach(ET entity, Hierarchy<ET> hierarchyToLoad);
}
