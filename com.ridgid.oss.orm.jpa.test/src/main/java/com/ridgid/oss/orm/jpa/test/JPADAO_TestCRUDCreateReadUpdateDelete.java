package com.ridgid.oss.orm.jpa.test;

import com.ridgid.oss.orm.PrimaryKeyedEntity;
import com.ridgid.oss.orm.jpa.JPAEntityCRUDCreateReadUpdateDelete;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public abstract class JPADAO_TestCRUDCreateReadUpdateDelete<DAO extends JPAEntityCRUDCreateReadUpdateDelete<ET, PKT>, ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPADAO_TestCRUDCreateReadUpdate<DAO, ET, PKT> {

    public JPADAO_TestCRUDCreateReadUpdateDelete(Class<ET> entityClass,
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

    public JPADAO_TestCRUDCreateReadUpdateDelete(Class<ET> entityClass,
                                                 Class<PKT> entityPrimaryKeyClass,
                                                 DAO dao,
                                                 String schemaName,
                                                 String tableName,
                                                 List<String> primaryKeyColumnAndFieldNames,
                                                 List<String> entityColumnAndFieldNames,
                                                 int numberOfTestRecords) {
        super(entityClass,
                entityPrimaryKeyClass,
                dao,
                schemaName,
                tableName,
                primaryKeyColumnAndFieldNames,
                entityColumnAndFieldNames,
                numberOfTestRecords);
    }

    @Test
    void when_remove_is_called_on_all_records_and_then_all_are_retrieved_the_count_is_zero() {
        setupTestEntities();
        for (ET rec : getDao().findAll(0, Integer.MAX_VALUE)) {
            getDao().delete(rec.getPk());
        }
        getEntityManager().flush();
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        int actual = getDao().findAll(0, Integer.MAX_VALUE).size();
        int expected = 0;
        assertEquals(expected, actual, "After all records removed there should be zero records");
    }
}
