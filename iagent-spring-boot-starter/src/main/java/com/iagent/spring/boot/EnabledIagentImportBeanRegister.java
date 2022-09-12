package com.iagent.spring.boot;

import com.iagent.spring.bind.IagentScannerConfigurer;
import com.iagent.util.ClassUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

public class EnabledIagentImportBeanRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes =
                importingClassMetadata.getAnnotationAttributes(EnabledIagent.class.getName());
        // 获取基础配置
        String[] packages = (String[]) annotationAttributes.get("value");
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(IagentScannerConfigurer.class);
        // 填充需扫描的接口路径
        beanDefinitionBuilder.addPropertyValue("basePackages", packages);
        registry.registerBeanDefinition(ClassUtils.getClassPathByClass(IagentScannerConfigurer.class), beanDefinitionBuilder.getBeanDefinition());
    }
}
