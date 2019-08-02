package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.Hierarchy;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base class for a JPA DAO that provides READ CRUD operations only
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides READ (find) CRUD methods for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class JPAEntityCRUDRead<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        implements
        JPAEntityCRUDDelegateRequired<ET, PKT>,
        JPAEntityCRUDReadDelegateRequired<ET, PKT> {

    private final JPAEntityCRUDReadDelegate<ET, PKT> baseDelegate;

    public JPAEntityCRUDRead(JPAEntityCRUDDelegate<ET, PKT> baseDelegate) {
        this.baseDelegate = new JPAEntityCRUDReadDelegate<>(baseDelegate);
    }

    public JPAEntityCRUDRead(Class<ET> classType,
                             Class<PKT> pkType) {
        this.baseDelegate = new JPAEntityCRUDReadDelegate<>(classType, pkType);
    }

    public JPAEntityCRUDRead(Class<ET> classType,
                             Class<PKT> pkType,
                             String pkName) {
        this.baseDelegate = new JPAEntityCRUDReadDelegate<>(classType, pkType, pkName);
    }

    public JPAEntityCRUDRead(Class<ET> classType,
                             Class<PKT> pkType,
                             short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDReadDelegate<>(classType, pkType, loadBatchSize);
    }

    public JPAEntityCRUDRead(Class<ET> classType,
                             Class<PKT> pkType,
                             String pkName,
                             short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDReadDelegate<>(classType, pkType, pkName, loadBatchSize);
    }

    public void setEntityManager(EntityManager entityManager) {
        baseDelegate.setEntityManager(entityManager);
    }

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
    public ET initializeAndDetach(ET entity, Hierarchy<ET> hierarchy) {
        return baseDelegate.initializeAndDetach(entity, hierarchy);
    }

    @Override
    public ET initialize(ET entity, Hierarchy<ET> hierarchy) {
        return baseDelegate.initialize(entity, hierarchy);
    }

    @Override
    public ET detach(ET entity, Hierarchy<ET> hierarchy) {
        return baseDelegate.detach(entity, hierarchy);
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
    public Optional<ET> optionalFind(PKT pk) throws EntityCRUDExceptionError {
        return baseDelegate.optionalFind(pk);
    }

    @Override
    public Optional<ET> optionalFind(PKT pk, Hierarchy<ET> hierarchy) throws EntityCRUDExceptionError {
        return baseDelegate.optionalFind(pk, hierarchy);
    }

    @Override
    public List<ET> findAll(int offset, int limit, Hierarchy<ET> hierarchy) throws EntityCRUDExceptionError {
        return baseDelegate.findAll(offset, limit, hierarchy);
    }
}
