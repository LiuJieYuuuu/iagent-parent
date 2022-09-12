package com.iagent.spring.boot;

import com.iagent.spring.IagentProxyBean;
import com.iagent.spring.bind.IagentScannerConfigurer;
import com.iagent.util.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(IagentProperties.class)
@ConditionalOnProperty(prefix = "iagent", value = "enabled",matchIfMissing = true)
public class IagentAutoConfiguration {

    @Autowired
    private IagentProperties properties;

    @Bean(name = IagentProxyBean.SPRING_BOOT_IAGENT_FACTORY_NAME)
    @ConditionalOnMissingBean
    public IagentProxyBean iagentProxyBean() {
        IagentProxyBean iagentProxyBean = new IagentProxyBean();
        if (StringUtils.isNotEmpty(properties.getJsonSupport())) {
            iagentProxyBean.setJsonSupport(properties.getJsonSupport());
        }
        if (StringUtils.isNotEmpty(properties.getLogImpl())) {
            iagentProxyBean.setLogImpl(properties.getLogImpl());
        }
        if (properties.getRequestConfig() != null) {
            iagentProxyBean.setRequestConfig(properties.getRequestConfig());
        }
        return iagentProxyBean;
    }

}
