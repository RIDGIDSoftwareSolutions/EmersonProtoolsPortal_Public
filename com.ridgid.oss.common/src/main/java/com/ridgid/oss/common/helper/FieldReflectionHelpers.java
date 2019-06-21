package com.ridgid.oss.common.helper;

import java.lang.reflect.Field;

/**
 *
 */
@SuppressWarnings({"JavaDoc", "WeakerAccess"})
public final class FieldReflectionHelpers {

    private FieldReflectionHelpers() {
    }

    /**
     * @param objClass
     * @param fieldName
     * @return
     */
    public static Field getFieldOrThrowRuntimeException(Class<?> objClass, String fieldName) {
        try {
            Field f = objClass.getDeclaredField(fieldName);
            enableFieldAccess(f);
            return f;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param obj
     * @param field
     * @return
     */
    public static Object getFieldValueOrThrowRuntimeException(Object obj, Field field) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param field
     */
    public static void enableFieldAccess(Field field) {
        if (!field.isAccessible()) field.setAccessible(true);
    }

    /**
     * @param fieldType
     * @param methodNameParameterArrayPairs
     * @return
     */
    public static boolean declaresAnyOfMethods(Class<?> fieldType,
                                               Object... methodNameParameterArrayPairs) {
        if (methodNameParameterArrayPairs.length % 2 != 0)
            throw new IllegalArgumentException("methodNameParameterArrayPairs must consist of one or more pairs of String methodName followed by Class<?>[]");
        for (int i = 0; i < methodNameParameterArrayPairs.length - 1; i = +2)
            if (declaresMethod
                    (
                            fieldType,
                            (String) methodNameParameterArrayPairs[i],
                            (Class<?>[]) methodNameParameterArrayPairs[i + 1]
                    ))
                return true;
        return false;
    }

    /**
     * @param fieldType
     * @param methodName
     * @param params
     * @return
     */
    public static boolean declaresMethod(Class<?> fieldType,
                                         String methodName,
                                         Class<?>... params) {
        try {
            fieldType.getDeclaredMethod(methodName, params);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
