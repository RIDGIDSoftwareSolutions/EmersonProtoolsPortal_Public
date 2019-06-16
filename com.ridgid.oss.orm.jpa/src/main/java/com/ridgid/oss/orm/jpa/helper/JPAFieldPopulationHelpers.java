package com.ridgid.oss.orm.jpa.helper;

import com.ridgid.oss.common.helper.FieldPopulationHelpers;

import java.lang.reflect.Field;

public final class JPAFieldPopulationHelpers {

    private JPAFieldPopulationHelpers() {
    }

    /**
     * @param idx
     * @param rec
     */
    public static void populateBaseFields(int idx,
                                          Object rec) {
        FieldPopulationHelpers.deterministicallyPopulateBaseFields
                (
                        idx,
                        rec,
                        JPAFieldReflectionHelpers::shouldExcludeBecauseIsSpecializedEntityField,
                        JPAFieldReflectionHelpers::shouldPopulateCompositeField,
                        JPAFieldReflectionHelpers::getTemporalTypeForAmbiguousTemporalField,
                        JPAFieldReflectionHelpers::getScaleOrLengthForField

                );
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     * @return
     */
    public static boolean populateBaseField(Object rec,
                                            Field field,
                                            int idx,
                                            int fieldIdx) {
        return FieldPopulationHelpers.deterministicallyPopulateBaseField
                (
                        rec,
                        field,
                        idx,
                        fieldIdx,
                        JPAFieldReflectionHelpers::shouldExcludeBecauseIsSpecializedEntityField,
                        JPAFieldReflectionHelpers::shouldPopulateCompositeField,
                        JPAFieldReflectionHelpers::getTemporalTypeForAmbiguousTemporalField,
                        JPAFieldReflectionHelpers::getScaleOrLengthForField
                );
    }

}
