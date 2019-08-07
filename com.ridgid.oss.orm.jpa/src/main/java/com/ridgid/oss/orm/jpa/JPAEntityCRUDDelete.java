package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionNotFound;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base class for a JPA DAO that provides DELETE CRUD operations only
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides a CREATE (add) CRUD method for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
@SuppressWarnings({"unused"})
public class JPAEntityCRUDDelete<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        implements
        JPAEntityCRUDDelegateRequired<ET, PKT>,
        JPAEntityCRUDDeleteDelegateRequired<ET, PKT> {

    private final JPAEntityCRUDDeleteDelegate<ET, PKT> baseDelegate;

    public JPAEntityCRUDDelete(JPAEntityCRUDDelegate<ET, PKT> baseDelegate) {
        this.baseDelegate = new JPAEntityCRUDDeleteDelegate<>(baseDelegate);
    }

    public JPAEntityCRUDDelete(Class<ET> classType,
                               Class<PKT> pkType) {
        this.baseDelegate = new JPAEntityCRUDDeleteDelegate<>(classType, pkType);
    }

    public JPAEntityCRUDDelete(Class<ET> classType,
                               Class<PKT> pkType,
                               String pkName) {
        this.baseDelegate = new JPAEntityCRUDDeleteDelegate<>(classType, pkType, pkName);
    }

    public JPAEntityCRUDDelete(Class<ET> classType,
                               Class<PKT> pkType,
                               short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDDeleteDelegate<>(classType, pkType, loadBatchSize);
    }

    public JPAEntityCRUDDelete(Class<ET> classType,
                               Class<PKT> pkType,
                               String pkName,
                               short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDDeleteDelegate<>(classType, pkType, pkName, loadBatchSize);
    }

    @Override
    public void setEntityManager(EntityManager entityManager) {
        baseDelegate.setEntityManager(entityManager);
    }

    @Override
    public EntityManager getEntityManager() {
        return baseDelegate.getEntityManager();
    }

    @Override
    public short getLoadBatchSize() {
        return baseDelegate.getLoadBatchSize();
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
    public ET initializeAndDetach(ET entity, HierarchyProcessor<ET> hierarchy) {
        return baseDelegate.initializeAndDetach(entity, hierarchy);
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
    public void delete(PKT pk, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError, EntityCRUDExceptionNotFound {
        baseDelegate.delete(pk, hierarchy);
    }
}
