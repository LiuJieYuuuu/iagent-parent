package com.iagent.proxy;

import com.iagent.config.IagentConfiguration;
import com.iagent.bean.IagentBeanWrapper;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * 接口代理处理器
 */
public class IagentProxyHandler {

    private static final Logger logger = LogFactory.getLogger(IagentProxyHandler.class);

    private IagentConfiguration configuration;

    public IagentProxyHandler(IagentConfiguration configuration) {
        this.configuration = configuration;
    }

    public Object invokeMethod(Method method, Object[] args) {
        IagentBeanWrapper wrapper = this.configuration.getBeanWrapperRegister().getBeanObject(ClassUtils.getClassPathByMethod(method));
        //拿到执行器，执行请求,返回结果
        Object result = null;
        try {
            result = wrapper.getExecutor().sendHttp(wrapper, args);
        } catch (Throwable throwable) {
            logger.error("The Method [" + ClassUtils.getClassPathByMethod(method) + "] Request Error:" + throwable.getMessage(), throwable);
            return result;
        }
        // 未找到则传null
        return result;
    }

}
