package com.iagent.spring.bind;

import com.iagent.annotation.IagentUrl;
import com.iagent.spring.IagentProxyBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.util.Set;

/**
 * 使用Spring Bean扫描器扫描指定包下得Bean
 */
public class ClassPathIagentScanner extends ClassPathBeanDefinitionScanner {

    public ClassPathIagentScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        addIncludeFilter(new AnnotationTypeFilter(IagentUrl.class));
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if (beanDefinitionHolders != null && !beanDefinitionHolders.isEmpty()) {
            processBeanDefinitionHolder(beanDefinitionHolders, basePackages);
        }
        return beanDefinitionHolders;
    }

    private void processBeanDefinitionHolder(Set<BeanDefinitionHolder> beanDefinitionHolders, String[] basePackages) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder bdh : beanDefinitionHolders) {
            definition = (GenericBeanDefinition) bdh.getBeanDefinition();
            String beanClassName = definition.getBeanClassName();
            definition.setBeanClass(IagentFactoryBean.class);
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            definition.getPropertyValues().add("iagentProxyBean", new RuntimeBeanReference(IagentProxyBean.SPRING_BOOT_IAGENT_FACTORY_NAME));
            definition.getPropertyValues().add("basePackages", basePackages);
            definition.setLazyInit(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

}
