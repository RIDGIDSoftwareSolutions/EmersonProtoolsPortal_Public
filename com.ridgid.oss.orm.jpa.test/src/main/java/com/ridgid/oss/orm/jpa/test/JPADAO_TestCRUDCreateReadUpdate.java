package com.ridgid.oss.orm.jpa.test;

import com.ridgid.oss.orm.PrimaryKeyedEntity;
import com.ridgid.oss.orm.jpa.JPAEntityCRUDCreateReadUpdate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public abstract class JPADAO_TestCRUDCreateReadUpdate<DAO extends JPAEntityCRUDCreateReadUpdate<ET, PKT>, ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPADAO_TestCRUDCreateRead<DAO, ET, PKT> {

    public JPADAO_TestCRUDCreateReadUpdate(Class<ET> entityClass,
                                           Class<PKT> entityPrimaryKeyClass,
                                           DAO dao,
                                           String tableName,
                                           List<String> primaryKeyColumnAndFieldNames,
                                           List<String> entityColumnAndFieldNames,
                                           int numberOfTestRecords) {
        super(entityClass,
                entityPrimaryKeyClass,
                dao,
                tableName,
                primaryKeyColumnAndFieldNames,
                entityColumnAndFieldNames,
                numberOfTestRecords);
    }

    @Test
    void when_update_is_called_on_all_existing_records_to_update_some_fields_the_records_read_back_reflect_the_changes() {
        setupTestEntities();
        for (ET rec : getAllEntitiesFromTestSet(getEntityClass())) {
            DAOTestHelpers.modifyFields(rec, getEntityFieldNames());
        }
        for (ET rec : getDao().findAll(0, Integer.MAX_VALUE)) {
            DAOTestHelpers.modifyFields(rec, getEntityFieldNames());
            getDao().update(rec);
        }
        getEntityManager().flush();
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        List<ET> actual = getDao().findAll(0, Integer.MAX_VALUE).stream().sorted(comparing(ET::getPk)).collect(toList());
        List<ET> expected = getAllEntitiesFromTestSet(getEntityClass()).stream().sorted(comparing(ET::getPk)).collect(toList());
        validateExpectedAndActualEntitiesAreAllEqual(expected, actual);
    }
}
