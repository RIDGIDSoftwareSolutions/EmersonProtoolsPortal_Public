package com.ridgid.oss.common.helper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public final class EqualityHelpers {

    private EqualityHelpers() {
    }

    /**
     * @param entityFieldNames
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean fieldsAreEqual(List<String> entityFieldNames,
                                         Object obj1,
                                         Object obj2,
                                         List<String> outErrors) {
        boolean areEqual = true;
        for (String fieldName : entityFieldNames) {
            Field field = FieldReflectionHelpers.getFieldOrThrowRuntimeException(obj1.getClass(), fieldName);
            areEqual = areEqual
                    &&
                    fieldIsEqual
                            (
                                    obj1,
                                    obj2,
                                    field,
                                    outErrors
                            );
        }
        return areEqual;
    }

    /**
     * @param obj1
     * @param obj2
     * @param outErrors
     * @return
     */
    private static boolean fieldsAreEqual(Object obj1,
                                          Object obj2,
                                          List<String> outErrors) {
        boolean areEqual = true;
        for (Field field : getAllFieldsFor(obj1.getClass())) {
            FieldReflectionHelpers.enableFieldAccess(field);
            areEqual = areEqual
                    &&
                    fieldIsEqual
                            (
                                    obj1,
                                    obj2,
                                    field,
                                    outErrors
                            );
        }
        return areEqual;
    }

    /**
     * @param obj1
     * @param obj2
     * @param field
     * @param outErrors
     * @return
     */
    public static boolean fieldIsEqual(Object obj1,
                                       Object obj2,
                                       Field field,
                                       List<String> outErrors) {
        int fieldModifiers = field.getModifiers();
        Class<?> fieldType = field.getType();
        if (Modifier.isStatic(fieldModifiers)
                || Modifier.isTransient(fieldModifiers)
                || Modifier.isVolatile(fieldModifiers)
                || Collection.class.isAssignableFrom(fieldType)
        )
            return true;
        Object value1 = FieldReflectionHelpers.getFieldValueOrThrowRuntimeException(obj1, field);
        Object value2 = FieldReflectionHelpers.getFieldValueOrThrowRuntimeException(obj2, field);
        if (!objectsAreEquivalent(fieldType, value1, value2, outErrors)) {
            outErrors.add
                    (
                            "Field Not Equal: " + field.getName()
                                    + ", Value Expected = " + value1
                                    + ", Actual Value = " + value2
                    );
            return false;
        }
        return true;
    }

    /**
     * @param objType
     * @param obj1
     * @param obj2
     * @param outErrors
     * @return
     */
    public static boolean objectsAreEquivalent(Class<?> objType, Object obj1, Object obj2, List<String> outErrors) {
        return obj1 == obj2
                ||
                (objType.isPrimitive() && Objects.equals(obj1, obj2))
                ||
                (objType.isArray() && Objects.deepEquals(obj1, obj2))
                ||
                (objType.isEnum() && Objects.equals(obj1, obj2))
                ||
                (FieldReflectionHelpers.declaresAnyOfMethods
                        (
                                objType,
                                "equals",
                                new Class<?>[]{objType},
                                "equals",
                                new Class<?>[]{Object.class}
                        )
                        && Objects.equals(obj1, obj2))
                ||
                (Serializable.class.isAssignableFrom(objType) && fieldsAreEqual(obj1, obj2, outErrors))
                ||
                (Comparable.class.isAssignableFrom(objType) && fieldsAreEqual(obj1, obj2, outErrors));
    }

    public static Iterable<? extends Field> getAllFieldsFor(Class<?> classType) {
        return null;
    }

    /**
     * @param entityFieldNames
     * @param childCollectionFieldNames
     * @param childCollectionTypeClasses
     * @param childEntityTypeClasses
     * @param obj1
     * @param obj2
     * @param outErrors
     * @return
     */
    public static boolean fieldsAreEqual(List<String> entityFieldNames,
                                         List<String> childCollectionFieldNames,
                                         List<Class<? extends Collection<?>>> childCollectionTypeClasses,
                                         List<Class<?>> childEntityTypeClasses,
                                         Object obj1,
                                         Object obj2,
                                         List<String> outErrors) {
        return
                fieldsAreEqual
                        (
                                entityFieldNames,
                                obj1,
                                obj2,
                                outErrors
                        )
                        &&
                        childCollectionsAreEqual
                                (
                                        childCollectionFieldNames,
                                        childCollectionTypeClasses,
                                        childEntityTypeClasses,
                                        obj1,
                                        obj2,
                                        outErrors
                                );
    }

    /**
     * @param childCollectionFieldNames
     * @param childCollectionTypeClasses
     * @param childEntityTypeClasses
     * @param obj1
     * @param obj2
     * @param outErrors
     * @return
     */
    public static boolean childCollectionsAreEqual(List<String> childCollectionFieldNames,
                                                   List<Class<? extends Collection<?>>> childCollectionTypeClasses,
                                                   List<Class<?>> childEntityTypeClasses,
                                                   Object obj1,
                                                   Object obj2,
                                                   List<String> outErrors) {
        boolean areEqual = true;
        for (int i = 0; i < childCollectionFieldNames.size(); i++) {
            String fName = childCollectionFieldNames.get(i);
            Class<?> cType = childCollectionTypeClasses.get(i);
            Class<?> eType = childEntityTypeClasses.get(i);
            Field field = FieldReflectionHelpers.getFieldOrThrowRuntimeException(obj1.getClass(), fName);
            if (cType.isAssignableFrom(field.getType())) {
                outErrors.add("Child Collection Type mismatch: Collection #" + i
                        + ", Name=" + fName
                        + ", Designated Type=" + cType
                        + ", Actual Type=" + field.getType());
                areEqual = false;
            }
            Object coll1 = FieldReflectionHelpers.getFieldValueOrThrowRuntimeException(obj1, field);
            Object coll2 = FieldReflectionHelpers.getFieldValueOrThrowRuntimeException(obj2, field);
            areEqual = areEqual
                    &&
                    collectionsAreEqual
                            (
                                    (Collection<?>) coll1,
                                    (Collection<?>) coll2,
                                    eType,
                                    fName,
                                    outErrors
                            );
        }
        return areEqual;
    }

    /**
     * @param coll1
     * @param coll2
     * @param eType
     * @param fName
     * @param outErrors
     * @return
     */
    public static boolean collectionsAreEqual(Collection<?> coll1,
                                              Collection<?> coll2,
                                              Class<?> eType,
                                              String fName,
                                              List<String> outErrors) {
        if (coll1.size() != coll2.size()) {
            outErrors.add
                    (
                            "Collection Sizes do not match: Collection Field Name=" + fName
                                    + ", Entity Type=" + eType
                    );
            return false;
        }
        boolean areEqual = true;
        Object[] arr1 = coll1.toArray();
        Object[] arr2 = coll2.toArray();
        for (int i = 0; i < arr1.length; i++)
            areEqual = areEqual
                    &&
                    fieldsAreEqual(arr1[i], arr2[i], outErrors);
        return areEqual;
    }

}