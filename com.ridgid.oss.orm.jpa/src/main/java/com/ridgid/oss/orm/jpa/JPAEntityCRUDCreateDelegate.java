package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.Hierarchy;
import com.ridgid.oss.orm.EntityCRUDCreate;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionAlreadyExists;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("unused")
final class JPAEntityCRUDCreateDelegate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        implements
        JPAEntityCRUDDelegateRequired<ET, PKT>,
        JPAEntityCRUDCreateDelegateRequired<ET, PKT>,
        EntityCRUDCreate<ET, PKT> {

    private final JPAEntityCRUDDelegate<ET, PKT> baseDelegate;

    JPAEntityCRUDCreateDelegate(JPAEntityCRUDDelegate<ET, PKT> baseDelegate) {
        this.baseDelegate = baseDelegate;
    }

    JPAEntityCRUDCreateDelegate(Class<ET> classType,
                                Class<PKT> pkType) {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType);
    }

    JPAEntityCRUDCreateDelegate(Class<ET> classType,
                                Class<PKT> pkType,
                                String pkName) {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType, pkName);
    }

    JPAEntityCRUDCreateDelegate(Class<ET> classType,
                                Class<PKT> pkType,
                                short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType, loadBatchSize);
    }

    JPAEntityCRUDCreateDelegate(Class<ET> classType,
                                Class<PKT> pkType,
                                String pkName,
                                short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType, pkName, loadBatchSize);
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
        return Optional.empty();
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

    /**
     * Adds the given entity to the persistence store (insert/create)
     *
     * @param entity the valid entity to store in the persistence layer that is not already created/inserted by primary key
     * @return the entity one any database or persistence layer modifications applied after successful create/insert
     * @throws EntityCRUDExceptionError         if there is an issue inserting/creating the record (specific "cause" may vary)
     * @throws EntityCRUDExceptionAlreadyExists if and entity one the same primary key of the given entity already exists in the persistent storage
     */
    @Override
    public ET add(ET entity, Hierarchy<ET> hierarchy) throws EntityCRUDExceptionError, EntityCRUDExceptionAlreadyExists {
        try {
            baseDelegate.getEntityManager().persist(entity);
            baseDelegate.getEntityManager().flush();
            baseDelegate.getEntityManager().refresh(entity);
            return entity;
        } catch (RuntimeException e) {
            throw baseDelegate.enhanceExceptionWithEntityManagerNullCheck(e);
        }
    }
}
