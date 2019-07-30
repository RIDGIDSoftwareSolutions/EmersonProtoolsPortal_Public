package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.GeneralVisitHandler;
import com.ridgid.oss.common.hierarchy.Hierarchy;
import com.ridgid.oss.common.hierarchy.VisitStatus;
import com.ridgid.oss.orm.EntityCRUD;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;
import com.ridgid.oss.orm.jpa.exception.EntityManagerNullException;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.ridgid.oss.common.hierarchy.Hierarchy.Traversal.DEPTH_FIRST;

@SuppressWarnings({"WeakerAccess", "unused"})
final class JPAEntityCRUDDelegate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        implements JPAEntityCRUDDelegateRequired<ET, PKT> {

    public final String PK_NAME;
    public final Class<ET> classType;
    public final Class<PKT> pkType;
    public final short loadBatchSize;

    EntityManager entityManager;
    CriteriaQuery<ET> entitiesForPrimaryKeysCriteriaQuery;

    public JPAEntityCRUDDelegate(Class<ET> classType,
                                 Class<PKT> pkType) {
        this.classType = classType;
        this.pkType = pkType;
        this.PK_NAME = "pk";
        this.loadBatchSize = 1000;
    }

    public JPAEntityCRUDDelegate(Class<ET> classType,
                                 Class<PKT> pkType,
                                 String pkName) {
        this.classType = classType;
        this.pkType = pkType;
        this.PK_NAME = pkName;
        this.loadBatchSize = 1000;
    }

    public JPAEntityCRUDDelegate(Class<ET> classType,
                                 Class<PKT> pkType,
                                 short loadBatchSize) {
        this.classType = classType;
        this.pkType = pkType;
        this.PK_NAME = "pk";
        this.loadBatchSize = loadBatchSize;
    }

    public JPAEntityCRUDDelegate(Class<ET> classType,
                                 Class<PKT> pkType,
                                 String pkName,
                                 short loadBatchSize) {
        this.classType = classType;
        this.pkType = pkType;
        this.PK_NAME = pkName;
        this.loadBatchSize = loadBatchSize;
    }

    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public final EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public final ET initializeAndDetach(ET entity,
                                        Hierarchy<ET> hierarchy) {
        visitEntityHierarchy
                (
                        entity,
                        hierarchy,
                        this::initializeEntityVisitHandler,
                        this::detachEntityVisitHandler
                );
        return entity;
    }

    @Override
    public final Optional<ET> load(PKT pk) {
        return Optional.ofNullable
                (
                        entityManager.find(classType, pk)
                );
    }

    @Override
    public final Stream<ET> loadBatch(List<PKT> pkList) {
        return entityManager
                .createQuery(getEntitiesForPrimaryKeysQuery())
                .setParameter("searchKeys", pkList)
                .getResultStream();
    }

    private CriteriaQuery<ET> getEntitiesForPrimaryKeysQuery() {
        if (entitiesForPrimaryKeysCriteriaQuery == null)
            synchronized (classType) {
                if (entitiesForPrimaryKeysCriteriaQuery == null) {
                    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
                    entitiesForPrimaryKeysCriteriaQuery = builder.createQuery(classType);
                    Root<ET> entity = entitiesForPrimaryKeysCriteriaQuery.from(classType);
                    Path<String> pk = entity.get(PK_NAME);
                    ParameterExpression<List> searchKeys = builder.parameter(List.class, "searchKeys");
                    entitiesForPrimaryKeysCriteriaQuery
                            .select(entity)
                            .where(pk.in(searchKeys));
                }
            }
        return entitiesForPrimaryKeysCriteriaQuery;
    }

    @Override
    public final short getLoadBatchSize() {
        return loadBatchSize;
    }

    @Override
    public final ET initialize(ET entity,
                               Hierarchy<ET> hierarchy) {
        visitEntityHierarchy
                (
                        entity,
                        hierarchy,
                        this::initializeEntityVisitHandler,
                        null
                );
        return entity;
    }

    @Override
    public final ET detach(ET entity,
                           Hierarchy<ET> hierarchy) {
        visitEntityHierarchy
                (
                        entity,
                        hierarchy,
                        EntityCRUD.NO_OP_VISIT_HANDLER,
                        this::detachEntityVisitHandler
                );
        return entity;
    }

    final RuntimeException enhanceExceptionWithEntityManagerNullCheck(Exception e) {
        if (entityManager == null)
            return new EntityManagerNullException(e);
        else
            return new EntityCRUDExceptionError(e);
    }

    private void visitEntityHierarchy(ET entity,
                                      Hierarchy<ET> hierarchy,
                                      GeneralVisitHandler visitor,
                                      GeneralVisitHandler afterChildrenVisitor) {
        try {
            if (hierarchy == null) {
                visitEntity
                        (
                                entity,
                                visitor,
                                afterChildrenVisitor
                        );
                return;
            }
            hierarchy.visit
                    (
                            entity,
                            visitor,
                            afterChildrenVisitor,
                            DEPTH_FIRST
                    );
        } catch (Exception e) {
            throw enhanceExceptionWithEntityManagerNullCheck(e);
        }
    }

    private void visitEntity(ET entity,
                             GeneralVisitHandler visitor,
                             GeneralVisitHandler afterChildrenVisitor) {
        visitor.handle(null, entity);
        if (afterChildrenVisitor != null)
            afterChildrenVisitor.handle(null, entity);
    }

    @SuppressWarnings("unused")
    private VisitStatus initializeEntityVisitHandler(Object p, Object o) {
        Hibernate.initialize(o);
        return VisitStatus.CONTINUE_PROCESSING;
    }

    @SuppressWarnings("unused")
    private VisitStatus detachEntityVisitHandler(Object p, Object o) {
        if (o instanceof PrimaryKeyedEntity) entityManager.detach(o);
        return VisitStatus.CONTINUE_PROCESSING;
    }
}