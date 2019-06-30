package com.ridgid.oss.common.helper;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 *
 */
@SuppressWarnings("JavaDoc")
public final class CollectionHelpers {

    private CollectionHelpers() {
    }

    /**
     * @param obj
     * @param fieldName
     */
    public static void clearCollection(Object obj, String fieldName) {
        Field field = FieldReflectionHelpers.getFieldOrThrowRuntimeException(obj.getClass(), fieldName);
        Collection<?> coll = (Collection<?>) FieldReflectionHelpers.getFieldValueOrThrowRuntimeException(obj, field);
        coll.removeIf(o -> true);
    }

}
