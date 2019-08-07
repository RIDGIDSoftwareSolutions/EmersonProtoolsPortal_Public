package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base class for a JPA DAO that provides READ CRUD operations only
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides an UPDATE (update) CRUD method for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
@SuppressWarnings({"unused"})
public class JPAEntityCRUDUpdate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        implements
        JPAEntityCRUDDelegateRequired<ET, PKT>,
        JPAEntityCRUDUpdateDelegateRequired<ET, PKT> {

    private final JPAEntityCRUDUpdateDelegate<ET, PKT> baseDelegate;

    public JPAEntityCRUDUpdate(JPAEntityCRUDUpdateDelegate<ET, PKT> baseDelegate) {
        this.baseDelegate = baseDelegate;
    }

    public JPAEntityCRUDUpdate(Class<ET> classType,
                               Class<PKT> pkType) {
        this.baseDelegate = new JPAEntityCRUDUpdateDelegate<>(classType, pkType);
    }

    public JPAEntityCRUDUpdate(Class<ET> classType, Class<PKT> pkType, String pkName) {
        this.baseDelegate = new JPAEntityCRUDUpdateDelegate<>(classType, pkType, pkName);
    }

    public JPAEntityCRUDUpdate(Class<ET> classType, Class<PKT> pkType, short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDUpdateDelegate<>(classType, pkType, loadBatchSize);
    }

    public JPAEntityCRUDUpdate(Class<ET> classType, Class<PKT> pkType, String pkName, short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDUpdateDelegate<>(classType, pkType, pkName, loadBatchSize);
    }

    public void setEntityManager(EntityManager entityManager) {
        baseDelegate.setEntityManager(entityManager);
    }

    public EntityManager getEntityManager() {
        return baseDelegate.getEntityManager();
    }

    @Override
    public ET initializeAndDetach(ET entity, HierarchyProcessor<ET> hierarchy) {
        return baseDelegate.initializeAndDetach(entity, hierarchy);
    }

    @Override
    public Optional<ET> load(PKT pk) {
        return baseDelegate.load(pk);
    }

    @Override
    public Stream<ET> loadBatch(List<PKT> pkList) {
        return baseDelegate.loadBatch(pkList);
    }

    @Override
    public short getLoadBatchSize() {
        return baseDelegate.getLoadBatchSize();
    }

    @Override
    public ET initialize(ET entity, HierarchyProcessor<ET> hierarchy) {
        return baseDelegate.initialize(entity, hierarchy);
    }

    @Override
    public ET detach(ET entity, HierarchyProcessor<ET> hierarchy) {
        return baseDelegate.detach(entity, hierarchy);
    }

    @Override
    public void flushContext() {
        baseDelegate.flushContext();
    }

    @Override
    public void clearContext() {
        baseDelegate.clearContext();
    }

    @Override
    public Class<ET> getClassType() {
        return baseDelegate.getClassType();
    }

    @Override
    public Class<PKT> getPkType() {
        return baseDelegate.getPkType();
    }

    @Override
    public Optional<ET> optionalUpdate(ET entity, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError {
        return baseDelegate.optionalUpdate(entity, hierarchy);
    }
}
