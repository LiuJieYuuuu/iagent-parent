package com.iagent.annotation;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER})
@Documented
public @interface PathKey {

    /**
     * value is key
     * @return
     */
    String value();

}
