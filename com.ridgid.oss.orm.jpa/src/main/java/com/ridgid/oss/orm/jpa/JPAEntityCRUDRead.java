package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDRead;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JPAEntityCRUDRead<T extends PrimaryKeyedEntity<PKT>, PKT> implements EntityCRUDRead<T, PKT> {

    private final Class<T> classType;
    private final Class<PKT> pkType;

    private EntityManager em;

    public JPAEntityCRUDRead(Class<T> classType,
                             Class<PKT> pkType) {
        this.classType = classType;
        this.pkType = pkType;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    protected final EntityManager getEm() {
        return em;
    }

    @Override
    public final T find(PKT pk) {
        return em.find(classType, pk, LockModeType.NONE);
    }

    @Override
    public final List<T> findAll(int offset, int limit) {
        CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(classType);
        Root<T> root = query.from(classType);
        return em.createQuery(query.select(root))
                .setLockMode(LockModeType.NONE)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

}
