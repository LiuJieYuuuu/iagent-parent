package com.iagent.proxy;

import com.iagent.config.IagentConfiguration;
import com.iagent.util.ClassUtils;

import java.lang.reflect.Proxy;

/**
 * <p>proxy implementation class</p>
 *
 * @see IagentProxy
 */
public class IagentProxyFactory {

    private IagentConfiguration configuration;

    public IagentProxyFactory(IagentConfiguration configuration){
        super();
        this.configuration = configuration;
    }

    /**
     * <b> create Object of interface by Dynamic Proxy </b>
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T newInstance(Class<T> cls){
        return (T) Proxy.newProxyInstance(ClassUtils.getClassLoader() ,new Class[]{cls}, new IagentProxy(configuration));
    }

}
