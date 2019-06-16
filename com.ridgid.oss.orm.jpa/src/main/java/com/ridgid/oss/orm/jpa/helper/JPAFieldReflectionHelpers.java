package com.ridgid.oss.orm.jpa.helper;

import com.ridgid.oss.common.helper.TemporalType;
import com.ridgid.oss.orm.CreateModifyTracking;

import javax.persistence.*;
import java.lang.reflect.Field;

import static com.ridgid.oss.common.helper.TemporalType.*;

@SuppressWarnings({"WeakerAccess", "unused", "JavaDoc"})
public final class JPAFieldReflectionHelpers {

    private JPAFieldReflectionHelpers() {
    }

    /**
     * @param field
     * @return
     */
    public static TemporalType getTemporalTypeForAmbiguousTemporalField(Field field) {
        return field.isAnnotationPresent(Temporal.class)
                ? getTemporalTypeForJPATemporalType(field)
                : TIMESTAMP;
    }

    public static javax.persistence.TemporalType
    getJPATemporalTypeForAmbiguousTemporalField(Field field) {
        return field.isAnnotationPresent(Temporal.class)
                ? field.getAnnotation(Temporal.class).value()
                : javax.persistence.TemporalType.TIMESTAMP;
    }

    /**
     * @param field
     * @return
     */
    public static TemporalType getTemporalTypeForJPATemporalType(Field field) {
        switch (field.getAnnotation(Temporal.class).value()) {
            case DATE:
                return DATE;
            case TIME:
                return TIME;
            case TIMESTAMP:
            default:
                return TIMESTAMP;
        }
    }

    /**
     * @param field
     * @return
     */
    public static boolean shouldExcludeBecauseIsSpecializedEntityField(Field field) {
        return field.isAnnotationPresent(Transient.class)
                || field.isAnnotationPresent(EmbeddedId.class)
                || field.isAnnotationPresent(Id.class);
    }

    /**
     * @param field
     * @return
     */
    public static int getScaleOrLengthForField(Field field) {
        return field.isAnnotationPresent(Column.class)
                ?
                (
                        field.getType().isArray() || field.getType() == String.class
                                ? field.getAnnotation(Column.class).length()
                                : field.getAnnotation(Column.class).scale()
                )
                : 0;
    }

    /**
     * @param field
     * @param fieldType
     * @return
     */
    public static boolean shouldPopulateCompositeField(Field field, Class<?> fieldType) {
        return
                (
                        field.isAnnotationPresent(Embedded.class)
                                ||
                                fieldType.isAnnotationPresent(Embeddable.class)
                )
                        &&
                        !CreateModifyTracking.class.isAssignableFrom(fieldType);
    }

}