package com.iagent.spring.annotation;

import com.iagent.constant.HttpConstant;
import com.iagent.constant.HttpEnum;
import com.iagent.request.ApacheHttpClientExecutor;
import com.iagent.request.HttpExecutor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <b>spring IDE scanner package</b>
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Import(value = {IagentImportBeanRegister.class})
public @interface IagentComponentScan {

    /**
     * value is packages
     * @return
     */
    String[] value();

    /**
     * 使用指定json处理
     * @return
     */
    String jsonSupport() default "";

    /**
     * 使用指定日志框架处理
     * @return
     */
    String logImpl() default "";

    /**
     * 全局默认执行器
     * 默认只使用第一个
     * @return
     */
    RequestConfig[] requestConfig() default {};

    /**
     * 默认注解处理解析器
     * @return
     */
    String resolver() default "Native";

    /**
     * 执行器默认配置
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface RequestConfig {

        /**
         * 默认请求类型
         * @return
         */
        HttpEnum requestType() default HttpEnum.GET;

        /**
         * 默认Content-Type
         * @return
         */
        String contentType() default HttpConstant.X_WWW_FORM_URLENCODED;

        /**
         * 默认链接时间
         * @return
         */
        int connectionTime() default HttpConstant.CONNECTION_TIME;

        /**
         * 默认读取时间
         * @return
         */
        int readTime() default HttpConstant.READ_TIME;

        /**
         * default http executor
         * @return
         */
        Class<? extends HttpExecutor> defaultHttpExecutor() default ApacheHttpClientExecutor.class;

    }
}
