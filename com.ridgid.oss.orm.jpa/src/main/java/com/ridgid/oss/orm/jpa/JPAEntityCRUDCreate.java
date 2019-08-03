package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionAlreadyExists;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base class for a JPA DAO that provides CREATE CRUD operations only
 *
 * @param <ET>  entity type of the entity that the DAO provides persistence methods for
 * @param <PKT> primary key type of the entity type that the DAO provides persistence methods for
 */
@SuppressWarnings({"unused"})
public class JPAEntityCRUDCreate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        implements
        JPAEntityCRUDDelegateRequired<ET, PKT>,
        JPAEntityCRUDCreateDelegateRequired<ET, PKT> {

    private final JPAEntityCRUDCreateDelegate<ET, PKT> baseDelegate;

    public JPAEntityCRUDCreate(JPAEntityCRUDDelegate<ET, PKT> baseDelegate) {
        this.baseDelegate = new JPAEntityCRUDCreateDelegate<>(baseDelegate);
    }

    public JPAEntityCRUDCreate(Class<ET> classType,
                               Class<PKT> pkType) {
        this.baseDelegate = new JPAEntityCRUDCreateDelegate<>(classType, pkType);
    }

    public JPAEntityCRUDCreate(Class<ET> classType,
                               Class<PKT> pkType,
                               String pkName) {
        this.baseDelegate = new JPAEntityCRUDCreateDelegate<>(classType, pkType, pkName);
    }

    public JPAEntityCRUDCreate(Class<ET> classType,
                               Class<PKT> pkType,
                               short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDCreateDelegate<>(classType, pkType, loadBatchSize);
    }

    public JPAEntityCRUDCreate(Class<ET> classType,
                               Class<PKT> pkType,
                               String pkName,
                               short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDCreateDelegate<>(classType, pkType, pkName, loadBatchSize);
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
    public Class<ET> getClassType() {
        return baseDelegate.getClassType();
    }

    @Override
    public Class<PKT> getPkType() {
        return baseDelegate.getPkType();
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
    public ET add(ET entity, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError, EntityCRUDExceptionAlreadyExists {
        return baseDelegate.add(entity, hierarchy);
    }
}
