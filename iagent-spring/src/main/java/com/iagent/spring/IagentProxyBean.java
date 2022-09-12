package com.iagent.spring;

import com.iagent.config.AbstractIagentFactory;
import com.iagent.config.DefaultIagentFactory;
import com.iagent.config.IagentConfiguration;
import com.iagent.request.RequestConfig;
import com.iagent.util.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * <b>Iagent BeanDefinitionRegistryPostProcessor
 * IagentFactoryBean dependency in Spring IOC</b>
 */
public class IagentProxyBean implements InitializingBean,DisposableBean {
 
    /**
     * 默认自动配置的Bean名称
     */
    public static final String SPRING_BOOT_IAGENT_FACTORY_NAME = "iagentProxyFactoryAutoConfiguration";

    //this HttpUriConf Object is Only one of Spring IOC
    private AbstractIagentFactory iagentFactory = null;

    private String[] basePackages;

    private String jsonSupport;

    private String logImpl;

    private RequestConfig requestConfig;

    public IagentProxyBean (){
        super();
    };

    public IagentProxyBean(String[] basePackages){
        this.basePackages = basePackages;
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }

    public void setJsonSupport(String jsonSupport) {
        this.jsonSupport = jsonSupport;
    }

    public void setLogImpl(String logImpl) {
        this.logImpl = logImpl;
    }

    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    public AbstractIagentFactory getIagentFactory() {
        return iagentFactory;
    }

    /**
     * Spring Bean Initialize
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() {
        if (iagentFactory != null) {
            // 说明已经初始化过了
            return;
        }
        if (basePackages != null) {
            IagentConfiguration configuration = new IagentConfiguration();
            configuration.setBasePackages(basePackages);

            if (StringUtils.isNotEmpty(logImpl)) {
                configuration.setLogImpl(logImpl);
            }

            if (StringUtils.isNotEmpty(jsonSupport)) {
                configuration.setJsonSupport(jsonSupport);
            }

            if (requestConfig != null) {
                configuration.setDefaultRequestConfig(requestConfig);
            }

            iagentFactory = new DefaultIagentFactory(configuration);
        }
        // 如果是空，那么久不加载初始化
    }

    /**
     * Spring Bean destory
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {

    }

}
