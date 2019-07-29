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

/**
 * Base Class for a DAO for a PrimaryKeyedEntity where the implementation of the DAO uses JPA and the entity is expected to have the needed JPA annotations
 * <p>
 * NOTE: This base class should not normally be inherited from directly. Instead, extend one of the JPAEntityCRUD* base classes that extend this class.
 *
 * @param <ET>  entity type of the entity that the DAO provides persistence methods for
 * @param <PKT> primary key type of the entity type that the DAO provides persistence methods for
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class JPAEntityCRUD<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        implements EntityCRUD<ET, PKT> {

    private final String PK_NAME;
    protected final Class<ET> classType;
    protected final Class<PKT> pkType;
    private final short loadBatchSize;

    public JPAEntityCRUD(Class<ET> classType,
                         Class<PKT> pkType) {
        this.classType = classType;
        this.pkType = pkType;
        this.PK_NAME = "pk";
        this.loadBatchSize = 1000;
    }

    public JPAEntityCRUD(Class<ET> classType,
                         Class<PKT> pkType,
                         String pkName) {
        this.classType = classType;
        this.pkType = pkType;
        this.PK_NAME = pkName;
        this.loadBatchSize = 1000;
    }

    public JPAEntityCRUD(Class<ET> classType,
                         Class<PKT> pkType,
                         short loadBatchSize) {
        this.classType = classType;
        this.pkType = pkType;
        this.PK_NAME = "pk";
        this.loadBatchSize = loadBatchSize;
    }

    public JPAEntityCRUD(Class<ET> classType,
                         Class<PKT> pkType,
                         String pkName,
                         short loadBatchSize) {
        this.classType = classType;
        this.pkType = pkType;
        this.PK_NAME = pkName;
        this.loadBatchSize = loadBatchSize;
    }

    private EntityManager entityManager;
    private CriteriaQuery<ET> entitiesForPrimaryKeysCriteriaQuery;

    /**
     * Sets the JPA entity manager used by the DAO
     *
     * @param entityManager that the DAO should use for all JPA Entity Manager actions
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Gets the JPA entity manager set for the DAO
     *
     * @return JPA EntityManager or null if not set
     */
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
                        NO_OP_VISIT_HANDLER,
                        this::detachEntityVisitHandler
                );
        return entity;
    }

    protected final RuntimeException enhanceExceptionWithEntityManagerNullCheck(Exception e) {
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
