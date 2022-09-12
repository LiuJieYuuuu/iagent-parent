package com.iagent.annotation;

import java.lang.annotation.*;

/**
 * @author liujieyu
 * @date 2022/5/16 21:15
 * @desciption 设置请求头参数注解
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER})
@Documented
public @interface ParamHeader {

    String value();

}
