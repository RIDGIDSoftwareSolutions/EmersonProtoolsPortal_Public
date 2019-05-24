package com.ridgid.oss.orm.jpa.test;

import com.ridgid.oss.orm.PrimaryKeyedEntity;
import com.ridgid.oss.orm.jpa.JPAEntityCRUDCreateRead;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.Query;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public abstract class JPADAO_TestCRUDCreateRead<DAO extends JPAEntityCRUDCreateRead<ET, PKT>, ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPADAO_TestCRUDRead<DAO, ET, PKT> {

    public JPADAO_TestCRUDCreateRead(Class<ET> entityClass,
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
    void when_add_is_called_it_retrieves_all_added_records_from_the_db() {
        Query query = createNativeDeleteQueryFrom(getTableName());
        assertEquals(0, getDao().findAll(0, 10).size(), "Should be 0 records found");
        for (ET rec : generateTestEntities()) {
            storeSetupRecord(rec);
            assertDoesNotThrow(() -> getDao().add(rec));
        }
        findAndCompareAllWithoutSetup();
    }
}
