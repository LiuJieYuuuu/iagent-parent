package com.iagent.spring.annotation;

import com.iagent.constant.HttpEnum;
import com.iagent.request.HttpExecutor;
import com.iagent.request.RequestConfig;
import com.iagent.spring.IagentProxyBean;
import com.iagent.spring.bind.IagentScannerConfigurer;
import com.iagent.util.ClassUtils;
import com.iagent.util.StringUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * <p>iagent dependency in Spring IOC by Spring @Import</p>
 */
public class IagentImportBeanRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String proxyFactoryRef = generateBaseBeanName(importingClassMetadata, 0);
        Map<String, Object> annotationAttributes =
                importingClassMetadata.getAnnotationAttributes(IagentComponentScan.class.getName());
        // 获取基础配置
        String[] packages = (String[]) annotationAttributes.get("value");
        String jsonSupport = (String) annotationAttributes.get("jsonSupport");
        String logImpl = (String) annotationAttributes.get("logImpl");
        String handleName = (String) annotationAttributes.get("resolver");
        // 获取基础配置
        RequestConfig requestConfig = EstablishRequestConfig((AnnotationAttributes[])annotationAttributes.get("requestConfig"));
        //if the ioc not have this bean
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(IagentProxyBean.class).getBeanDefinition();
        if (packages != null) {
            beanDefinition.getPropertyValues().add("basePackages", packages);
        }

        if (StringUtils.isNotEmpty(jsonSupport)) {
            beanDefinition.getPropertyValues().add("jsonSupport", jsonSupport);
        }

        if (StringUtils.isNotEmpty(logImpl)) {
            beanDefinition.getPropertyValues().add("logImpl", logImpl);
        }

        if (StringUtils.isNotEmpty(handleName)) {
            // do something
        }
        beanDefinition.getPropertyValues().add("requestConfig", requestConfig);
        registry.registerBeanDefinition(proxyFactoryRef, beanDefinition);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(IagentScannerConfigurer.class);
        beanDefinitionBuilder.addConstructorArgReference(proxyFactoryRef);
        registry.registerBeanDefinition(ClassUtils.getClassPathByClass(IagentScannerConfigurer.class), beanDefinitionBuilder.getBeanDefinition());
    }

    private String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
        return importingClassMetadata.getClassName() + "#" + IagentProxyBean.class.getSimpleName() + "#" + index;
    }

    /**
     * 根据注解构建出一个RequestConfig对象
     * @see com.iagent.request.RequestConfig
     * @return
     */
    private RequestConfig EstablishRequestConfig(AnnotationAttributes[] annotationAttributes) {
        if (annotationAttributes.length == 0) {
            return new RequestConfig(HttpExecutor.DEFAULT_EXECUTOR);
        }
        int connectionTime = annotationAttributes[0].getNumber("connectionTime");
        String contentType = annotationAttributes[0].getString("contentType");
        Class<? extends HttpExecutor> defaultHttpExecutor = annotationAttributes[0].getClass("defaultHttpExecutor");
        int readTime = annotationAttributes[0].getNumber("readTime");
        HttpEnum requestType = annotationAttributes[0].getEnum("requestType");
        RequestConfig config = new RequestConfig(defaultHttpExecutor);
        config.setConnectionTime(connectionTime);
        config.setContentType(contentType);
        config.setReadTime(readTime);
        config.setRequestType(requestType);

        return config;
    }
}
