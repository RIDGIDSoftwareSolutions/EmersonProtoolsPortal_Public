package com.ridgid.oss.common.helper;

import java.lang.reflect.Field;

/**
 *
 */
@SuppressWarnings({"JavaDoc", "WeakerAccess"})
public final class ExceptionHelpers {

    private ExceptionHelpers() {
    }

    /**
     * @param obj
     * @param field
     * @param idx
     * @param fieldIdx
     * @param e
     */
    public static void throwAsRuntimeExceptionUnableToSetField(Object obj,
                                                               Field field,
                                                               int idx,
                                                               int fieldIdx,
                                                               Exception e) {
        throw new RuntimeException
                (
                        "Unable to set field value:"
                                + " Entity=" + obj.getClass().getName()
                                + ", Field=" + field.getName()
                                + ", idx=" + idx
                                + ", fieldIdx=" + fieldIdx,
                        e
                );
    }

    /**
     * @param obj
     * @param field
     * @param e
     */
    public static void throwAsRuntimeExceptionUnableToSetField(Object obj,
                                                               Field field,
                                                               Exception e) {
        throw new RuntimeException
                (
                        "Unable to modify field value:"
                                + " Entity=" + obj.getClass().getName()
                                + ", Field=" + field.getName(),
                        e
                );
    }

//    public static <RTE extends RuntimeException> RTE wrapExceptions() {
//
//    }
}
