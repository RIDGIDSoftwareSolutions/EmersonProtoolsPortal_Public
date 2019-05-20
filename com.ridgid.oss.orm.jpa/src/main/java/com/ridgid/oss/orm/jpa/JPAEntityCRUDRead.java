package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDRead;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JPAEntityCRUDRead<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends JPAEntityCRUD<ET, PKT>
        implements EntityCRUDRead<ET, PKT> {

    private final Class<ET> classType;
    private final Class<PKT> pkType;

    protected JPAEntityCRUDRead(Class<ET> classType,
                                Class<PKT> pkType) {
        this.classType = classType;
        this.pkType = pkType;
    }

    @Override
    public final ET find(PKT pk) {
        return getEntityManager().find(classType, pk, LockModeType.NONE);
    }

    @Override
    public final List<ET> findAll(int offset, int limit) {
        CriteriaQuery<ET> query = getEntityManager().getCriteriaBuilder().createQuery(classType);
        Root<ET> root = query.from(classType);
        return getEntityManager().createQuery(query.select(root))
                .setLockMode(LockModeType.NONE)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

}
