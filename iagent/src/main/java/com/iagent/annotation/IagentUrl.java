package com.iagent.annotation;

import com.iagent.constant.HttpConstant;
import com.iagent.constant.HttpEnum;
import com.iagent.request.AbstractHttpExecutor;
import com.iagent.request.ApacheHttpClientExecutor;
import com.iagent.request.HttpExecutor;
import com.iagent.request.SimpleHttpExecutor;

import java.lang.annotation.*;

/**
 * <p>create uri</p>
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD,ElementType.TYPE})
@Documented
public @interface IagentUrl {

    /**
     * value is url
     * @return
     */
    String value() default "";

    HttpEnum[] requestType() default {};

    String contentType() default "";

    int connectionTime() default -1;

    int readTime() default -1;

    /**
     * default use to SimpleHttpExecutor.java
     * @return
     */
    Class<? extends HttpExecutor> httpExecutor() default ApacheHttpClientExecutor.class;

}
