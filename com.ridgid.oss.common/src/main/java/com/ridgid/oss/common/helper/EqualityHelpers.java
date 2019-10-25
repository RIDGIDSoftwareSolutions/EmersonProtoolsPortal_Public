package com.ridgid.oss.common.helper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings({"WeakerAccess", "JavaDoc", "unused"})
public final class EqualityHelpers
{

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
                                         List<String> outErrors)
    {
        boolean areEqual = true;
        for (String fieldName : entityFieldNames) {

            Optional<Map.Entry<Object, Field>> objectField
                = FieldReflectionHelpers.determineObjectAndFieldForPathIntoObject(
                obj1, fieldName);
            Optional<Object> valueObj1 = objectField.map(Map.Entry::getKey);
            Optional<Field>  field     = objectField.map(Map.Entry::getValue);

            objectField = FieldReflectionHelpers.determineObjectAndFieldForPathIntoObject(obj2, fieldName);
            Optional<Object> valueObj2 = objectField.map(Map.Entry::getKey);
            if (!field.isPresent()) field = objectField.map(Map.Entry::getValue);

            areEqual = areEqual
                       &&
                       (!valueObj1.isPresent() && !valueObj2.isPresent())
                       ||
                       (
                           field.isPresent()
                           &&
                           fieldIsEqual
                               (
                                   valueObj1.orElse(null),
                                   valueObj2.orElse(null),
                                   field.get(),
                                   outErrors
                               )
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
                                          List<String> outErrors)
    {
        boolean areEqual = true;
        for (Field field : FieldReflectionHelpers.getAllNonStaticFieldsFor(obj1.getClass())) {
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
                                       List<String> outErrors)
    {
        if ((obj1 == null && obj2 != null)
            ||
            (obj1 != null && obj2 == null))
            return false;

        int      fieldModifiers = field.getModifiers();
        Class<?> fieldType      = field.getType();
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
                                         List<String> outErrors)
    {
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
                                                   List<String> outErrors)
    {
        boolean areEqual = true;
        for (int i = 0; i < childCollectionFieldNames.size(); i++) {
            String   fName = childCollectionFieldNames.get(i);
            Class<?> cType = childCollectionTypeClasses.get(i);
            Class<?> eType = childEntityTypeClasses.get(i);
            Field    field = FieldReflectionHelpers.getFieldOrThrowRuntimeException(obj1.getClass(), fName);
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
                                              List<String> outErrors)
    {
        if (coll1.size() != coll2.size()) {
            outErrors.add
                (
                    "Collection Sizes do not match: Collection Field Name=" + fName
                    + ", Entity Type=" + eType
                );
            return false;
        }
        boolean  areEqual = true;
        Object[] arr1     = coll1.toArray();
        Object[] arr2     = coll2.toArray();
        for (int i = 0; i < arr1.length; i++)
             areEqual = areEqual
                        &&
                        fieldsAreEqual(arr1[i], arr2[i], outErrors);
        return areEqual;
    }

    /**
     * @param entityFieldNames
     * @param coll1
     * @param coll2
     * @return
     */
    public static <T> Optional<String> collectionElementFieldsAreEqual(List<String> entityFieldNames,
                                                                       Collection<T> coll1,
                                                                       Collection<T> coll2)
    {
        if (coll1.size() != coll2.size()) return Optional.of("Collection sizes do not match");
        StringBuilder errors = new StringBuilder();

        for (Iterator<T> item1 = coll1.iterator(), item2 = coll2.iterator();
             item1.hasNext(); ) {
            List<String> errorList = new ArrayList<>();
            EqualityHelpers.fieldsAreEqual(
                entityFieldNames,
                item1.next(),
                item2.next(),
                errorList);
            errorList.forEach(error -> errors.append(error).append("\n"));
        }

        return errors.length() > 0
               ? Optional.of(errors.toString())
               : Optional.empty();
    }
}