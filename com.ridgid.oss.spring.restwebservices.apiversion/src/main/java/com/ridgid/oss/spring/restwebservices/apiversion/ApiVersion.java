package com.ridgid.oss.spring.restwebservices.apiversion;

import java.lang.annotation.*;

/**
 * Can be applied to either the class or method level
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface ApiVersion {
    int EARLIEST_SUPPORTED_API_VERSION = 1;
    int CURRENT_API_VERSION = 3;

    int since() default EARLIEST_SUPPORTED_API_VERSION;

    int until() default CURRENT_API_VERSION + 1;
}
