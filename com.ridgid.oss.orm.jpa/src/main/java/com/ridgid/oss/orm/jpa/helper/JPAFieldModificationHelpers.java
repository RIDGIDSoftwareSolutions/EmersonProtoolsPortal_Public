package com.ridgid.oss.orm.jpa.helper;

import com.ridgid.oss.common.helper.FieldModificationHelpers;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Set;


public final class JPAFieldModificationHelpers {

    private JPAFieldModificationHelpers() {
    }

    /**
     * @param obj
     * @param fieldNames
     */
    public static void modifyFields(Object obj,
                                    List<String> fieldNames) {
        FieldModificationHelpers.deterministicallyModifyFields
                (
                        obj,
                        fieldNames,
                        Collections.emptySet(),
                        JPAFieldReflectionHelpers::shouldExcludeBecauseIsSpecializedEntityField,
                        JPAFieldReflectionHelpers::getTemporalTypeForAmbiguousTemporalField
                );
    }

    /**
     * @param obj
     * @param fieldNames
     * @param foreignKeyFieldNames
     */
    public static void modifyFields(Object obj,
                                    List<String> fieldNames,
                                    Set<String> foreignKeyFieldNames) {
        FieldModificationHelpers.deterministicallyModifyFields
                (
                        obj,
                        fieldNames,
                        foreignKeyFieldNames,
                        JPAFieldReflectionHelpers::shouldExcludeBecauseIsSpecializedEntityField,
                        JPAFieldReflectionHelpers::getTemporalTypeForAmbiguousTemporalField
                );
    }

    /**
     * @param obj
     * @param field
     */
    public static void modifyField(Object obj,
                                   Field field) {
        FieldModificationHelpers.deterministicallyModifyField
                (
                        obj,
                        field,
                        JPAFieldReflectionHelpers::shouldExcludeBecauseIsSpecializedEntityField,
                        JPAFieldReflectionHelpers::getTemporalTypeForAmbiguousTemporalField
                );
    }


}
