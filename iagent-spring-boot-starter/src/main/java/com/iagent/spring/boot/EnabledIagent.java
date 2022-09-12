package com.iagent.spring.boot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Spring Boot Enabled Auto Annotation
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Import(value = {EnabledIagentImportBeanRegister.class})
public @interface EnabledIagent {

    /**
     * 扫描包路径
     * @return
     */
    String[] value();
}
