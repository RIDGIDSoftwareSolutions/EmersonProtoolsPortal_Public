package com.ridgid.oss.orm;

import com.ridgid.oss.common.helper.StreamHelpers;
import com.ridgid.oss.common.hierarchy.GeneralVisitHandler;
import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.common.hierarchy.VisitStatus;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Provides marker interface to indicate a DAO that is a valid EntityCRUD DAO for Entity Type ET where ET is a PrimaryKeyedEntity one Primary Key Type PKT
 *
 * @param <ET>  Entity Type of Entity that the DAO is providing CRUD methods for
 * @param <PKT> Type of the Primary Key of the Entity Type
 */
@SuppressWarnings("unused")
public interface EntityCRUD<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>> {

    GeneralVisitHandler NO_OP_VISIT_HANDLER = (p, o) -> VisitStatus.OK_CONTINUE;

    default List<ET> initialize(Stream<ET> entityStream, HierarchyProcessor<ET> hierarchy) {
        return entityStream.map(e -> initialize(e, hierarchy)).collect(toList());
    }

    default List<ET> initialize(Stream<ET> entityStream) {
        return entityStream.map(this::initialize).collect(toList());
    }

    /**
     * Load and initialize Lazily-Loaded fields and dependencies of the given entity, then, detach the enity graph
     * from the Persistence Context and return it
     *
     * @param entity    to load lazy loaded fields and lazy loaded dependencies for
     * @param hierarchy hierarchy with the given entity as the root to load
     * @return detached entity graphy rooted at the given entity with all lazy-loaded fields and dependencies named in the graph initalized
     */
    default ET initializeAndDetach(ET entity, HierarchyProcessor<ET> hierarchy) {
        return detach(initialize(entity, hierarchy), hierarchy);
    }

    default ET initializeAndDetach(ET entity) {
        return initializeAndDetach(entity, null);
    }

    default List<ET> initializeAndDetach(Stream<ET> entityStream, HierarchyProcessor<ET> hierarchy) {
        return entityStream.map(e -> initializeAndDetach(e, hierarchy)).collect(toList());
    }

    default List<ET> initializeAndDetach(Stream<ET> entityStream) {
        return entityStream.map(this::initializeAndDetach).collect(toList());
    }


    default List<ET> loadInitializeAndDetach(Stream<PKT> pktStream, HierarchyProcessor<ET> hierarchy) {
        return initializeAndDetach(load(pktStream), hierarchy);
    }

    default List<ET> loadInitializeAndDetach(Stream<PKT> pktStream) {
        return initializeAndDetach(load(pktStream));
    }

    default Stream<ET> loadAndInitialize(Stream<PKT> pktStream, HierarchyProcessor<ET> hierarchy) {
        return load(pktStream).map(e -> initialize(e, hierarchy));
    }

    default Stream<ET> loadAndInitialize(Stream<PKT> pktStream) {
        return loadAndInitialize(pktStream, null);
    }

    default Stream<ET> load(Stream<PKT> pktStream) {
        if (getLoadBatchSize() > 1)
            return Stream
                    .concat
                            (
                                    pktStream.filter(Objects::nonNull),
                                    Stream.of((PKT) null)
                            )
                    .flatMap
                            (
                                    StreamHelpers.group
                                            (
                                                    getLoadBatchSize(),
                                                    (PKT) null
                                            )
                            )
                    .flatMap
                            (
                                    this::loadBatch
                            );
        else
            return pktStream
                    .filter
                            (
                                    Objects::nonNull
                            )
                    .map
                            (
                                    this::load
                            )
                    .filter
                            (
                                    Optional::isPresent
                            )
                    .map
                            (
                                    Optional::get
                            );
    }

    Optional<ET> load(PKT pk);

    Stream<ET> loadBatch(List<PKT> pkList);

    short getLoadBatchSize();

    ET initialize(ET entity, HierarchyProcessor<ET> hierarchy);

    default ET initialize(ET entity) {
        return initialize(entity, null);
    }

    ET detach(ET entity, HierarchyProcessor<ET> hierarchy);

    default ET detach(ET entity) {
        return detach(entity, null);
    }

}
