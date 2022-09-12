package com.iagent.scan.resolver;

import com.iagent.annotation.IagentUrl;
import com.iagent.config.IagentConfiguration;
import com.iagent.bean.IagentBeanWrapper;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.request.AbstractHttpExecutor;
import com.iagent.request.HttpExecutor;
import com.iagent.request.RequestConfig;
import com.iagent.scan.resolver.annotation.AbstractParameterResolver;
import com.iagent.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * @author liujieyu
 * @date 2022/5/27 21:19
 * @desciption
 */
public abstract class AbstractInterfaceAnnotationResolver extends AbstractParameterResolver implements InterfaceAnnotationResolver {

    private static final Logger logger = LogFactory.getLogger(AbstractInterfaceAnnotationResolver.class);

    private final IagentConfiguration configuration;

    public AbstractInterfaceAnnotationResolver(IagentConfiguration configuration) {
        super(configuration);
        this.configuration = configuration;
    }

    @Override
    public boolean existsIagentAnnotation(Method method) {
        return method.getAnnotation(IagentUrl.class) != null;
    }

    /**
     * 注册指定执行器实例
     * @param executorClass
     * @param httpExecutor
     */
    public void registerHttpExecutor(Class<? extends HttpExecutor> executorClass, HttpExecutor httpExecutor) {
        this.configuration.getExecutorRegister().
                registerBean(ClassUtils.getClassPathByClass(executorClass), httpExecutor);
    }

    /**
     * 是否包含某个执行器
     * @param executorClass
     * @return
     */
    public boolean containHttpExecutor(Class<? extends HttpExecutor> executorClass) {
        if (!AbstractHttpExecutor.class.isAssignableFrom(executorClass)) {
            logger.warn("The Http Executor is Not extends AbstractHttpExecutor!");
        }
        return this.configuration.getExecutorRegister()
                .containBeanName(ClassUtils.getClassPathByClass(executorClass));
    }

    /**
     * 获取指定执行器实例
     * @param executorClass
     * @return
     */
    public HttpExecutor getHttpExecutor(Class<? extends HttpExecutor> executorClass) {
        return this.configuration.getExecutorRegister().getBeanObject(ClassUtils.getClassPathByClass(executorClass));
    }
    /**
     * 注册方法包装类
     * @param method
     * @param wrapper
     */
    public void registerBeanWrapper(Method method, IagentBeanWrapper wrapper) {
        this.configuration.getBeanWrapperRegister().registerBean(ClassUtils.getClassPathByMethod(method), wrapper);
    }

    /**
     * get default config
     * @return
     */
    public RequestConfig getDefaultRequestConfig() {
        return this.configuration.getDefaultRequestConfig();
    }

    public IagentConfiguration getConfiguration() {
        return configuration;
    }
}
