package com.iagent.annotation;

import java.lang.annotation.*;

/**
 * POST application/json JSON parameters
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER})
@Documented
public @interface ParamBody {
    /**
     * required parameters
     * @return
     */
   boolean required() default false;
}
