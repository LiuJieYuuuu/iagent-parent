package com.iagent.annotation;

import com.iagent.constant.HttpConstant;
import com.iagent.util.CollectionUtils;

import java.lang.annotation.*;
import java.util.List;

/**
 * @author liujieyu
 * @date 2022/9/9 18:35
 * @desciption 用于排序
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
public @interface Order {

    /**
     * 排序比重
     * @see CollectionUtils#sortByOrder(List)
     * @return
     */
    int value() default HttpConstant.MIN_ORDER;
}
