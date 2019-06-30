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

    public static int comparing(Comparable... objects) {
        if (objects.length % 2 != 0 || objects.length < 2)
            throw new IllegalArgumentException("Number of Objects to Compare must be even and there must be at least 2 objects to compare");
        int rv = 0;
        for (int i = 0; i < objects.length; i += 2) {
            //noinspection unchecked
            rv = objects[i].compareTo(objects[i + 1]);
            if (rv != 0) return rv;
        }
        return rv;
    }

}
