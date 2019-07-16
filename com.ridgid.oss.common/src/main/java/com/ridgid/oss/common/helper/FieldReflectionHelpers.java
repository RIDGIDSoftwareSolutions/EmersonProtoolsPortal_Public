package com.ridgid.oss.common.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 *
 */
@SuppressWarnings({"JavaDoc", "WeakerAccess", "unused"})
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
     * @param fieldName
     * @return
     */
    public static Optional<Map.Entry<Object, Field>> determineObjectAndFieldForPathIntoObject(Object obj, String fieldName) {
        if (obj == null) return Optional.empty();
        String[] fieldNamePath = fieldName.split("\\.");
        Object valueObj = obj;
        fieldName = fieldNamePath[0];
        for (int j = 1; j < fieldNamePath.length; j++) {
            Field f = FieldReflectionHelpers.getFieldOrThrowRuntimeException(valueObj.getClass(), fieldName);
            valueObj = FieldReflectionHelpers.getFieldValueOrThrowRuntimeException(valueObj, f);
            fieldName = fieldNamePath[j];
        }
        if (valueObj == null) return Optional.empty();
        Field f = FieldReflectionHelpers.getFieldOrThrowRuntimeException(valueObj.getClass(), fieldName);
        return Optional.of(new AbstractMap.SimpleEntry<>(valueObj, f));
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
     * @param entity
     * @param field
     * @param fieldValue
     */
    public static void setFieldValueOrThrowException(Object entity, Field field, Object fieldValue) {
        try {
            field.set(entity, fieldValue);
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


    /**
     * @param classType
     * @return
     */
    public static Iterable<? extends Field> getAllNonStaticFieldsFor(Class<?> classType) {
        return streamAllNonStaticFieldsFor(classType).collect(toList());
    }

    /**
     * @param classType
     * @return
     */
    public static Stream<Field> streamAllNonStaticFieldsFor(Class<?> classType) {
        Stream<Field> fields = Arrays.stream(classType.getDeclaredFields());
        for (Class<?> clazz = classType.getSuperclass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass())
            fields = Stream.concat(fields, Arrays.stream(clazz.getDeclaredFields()));
        return fields
                .peek(f -> {
                    if (!f.isAccessible()) f.setAccessible(true);
                })
                .filter
                        (
                                f -> !(Modifier.isStatic(f.getModifiers())
                                        || Modifier.isFinal(f.getModifiers())
                                        || Modifier.isTransient(f.getModifiers())
                                        || Modifier.isVolatile(f.getModifiers())
                                        || Modifier.isNative(f.getModifiers()))
                        );
    }

    /**
     * Applies
     *
     * @param obj               object whose fields should have the designated fieldValueHandler operation applied to
     * @param fieldValueHandler performs whatever operation is needed on the target field value (Object) and then return the Object if it needs recursed into; otherwise return null.
     */
    public static void applyToFieldsRecursively(Object obj,
                                                Function<Object, Object> fieldValueHandler) {
        Set<Object> objectsVisited = new HashSet<>();
        applyToFieldsRecursively(objectsVisited, obj, fieldValueHandler);
    }

    private static void applyToFieldsRecursively(Set<Object> objectsVisited,
                                                 Object obj,
                                                 Function<Object, Object> fieldValueHandler) {
        for (Field field : getAllNonStaticFieldsFor(obj.getClass())) {
            Object fieldValue = getFieldValueOrThrowRuntimeException(obj, field);
            if (fieldValue != null) {
                Object updatedObject = fieldValueHandler.apply(fieldValue);
                if (updatedObject != null) {
                    setFieldValueOrThrowException(obj, field, updatedObject);
                    if (!objectsVisited.contains(updatedObject))
                        applyToFieldsRecursively(objectsVisited, updatedObject, fieldValueHandler);
                }
                objectsVisited.add(updatedObject);
            }
        }
    }

}
