package com.iagent.spring.bind;

import com.iagent.config.IagentConfiguration;
import com.iagent.spring.IagentProxyBean;
import com.iagent.util.ClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.List;

/**
 * iagent 自动配置接口类
 */
public class IagentScannerConfigurer implements BeanDefinitionRegistryPostProcessor {

    private IagentProxyBean iagentProxyBean;

    private String[] basePackages;

    public IagentScannerConfigurer() {}

    public IagentScannerConfigurer(IagentProxyBean iagentProxyBean) {
        this.iagentProxyBean = iagentProxyBean;
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (!registry.containsBeanDefinition(IagentProxyBean.SPRING_BOOT_IAGENT_FACTORY_NAME)) {
            IagentConfiguration configuration = iagentProxyBean.getIagentFactory().getConfiguration();
            List<Class> proxyClassList = configuration.getProxyClassList();
            for (Class<?> clazz : proxyClassList){
                //create bean definition by class
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(IagentFactoryBean.class);
                beanDefinitionBuilder.addPropertyValue("iagentFactory", iagentProxyBean.getIagentFactory());
                beanDefinitionBuilder.addConstructorArgValue(clazz);
                //dependency in Spring IOC
                registry.registerBeanDefinition(ClassUtils.getClassPathByClass(clazz), beanDefinitionBuilder.getBeanDefinition());
            }
        } else {
            // Auto Configuration run Spring scanner
            ClassPathIagentScanner classPathIagentScanner = new ClassPathIagentScanner(registry);
            if (basePackages == null) {
                basePackages = iagentProxyBean.getIagentFactory().getConfiguration().getBasePackages();
            }
            classPathIagentScanner.scan(basePackages);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // do something
    }

}
