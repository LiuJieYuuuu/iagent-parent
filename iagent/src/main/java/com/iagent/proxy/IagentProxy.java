package com.iagent.proxy;

import com.iagent.config.IagentConfiguration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * <p>dynamic proxy implementation class ,it's InvocationHandler Class</p>
 *
 * @see IagentProxyFactory
 */
public class IagentProxy implements InvocationHandler {

    private IagentConfiguration configuration;

    public IagentProxy(IagentConfiguration configuration){
        this.configuration = configuration;
    }

    /**
     * <p>Dynamic Proxy real run function</p>
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(method.getDeclaringClass())){
            //object 类的方法直接运行即可
            return method.invoke(this,args);
        }else{
            // 即接口代理方法
            return this.configuration.getProxyHandle().invokeMethod(method, args);
        }
    }

}
