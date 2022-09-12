package com.iagent.config;

import com.iagent.bean.IagentBeanWrapper;
import com.iagent.json.JSON;
import com.iagent.json.JSONSupport;
import com.iagent.json.fastjson.FastJsonSupport;
import com.iagent.json.gson.GsonSupport;
import com.iagent.json.jackson.JacksonSupport;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.logging.commons.JakartaCommonsLoggingImpl;
import com.iagent.logging.console.ConsoleImpl;
import com.iagent.logging.jdk14.Jdk14LoggingImpl;
import com.iagent.logging.log4j.Log4jImpl;
import com.iagent.logging.log4j2.Log4j2Impl;
import com.iagent.logging.nologging.NoLoggingImpl;
import com.iagent.logging.slf4j.Slf4jImpl;
import com.iagent.proxy.IagentProxyHandler;
import com.iagent.register.AliasNameRegister;
import com.iagent.register.BeanRegister;
import com.iagent.register.GenericBeanRegister;
import com.iagent.request.HttpExecutor;
import com.iagent.request.RequestConfig;
import com.iagent.request.parse.BinaryResultParser;
import com.iagent.request.parse.NumberResultParser;
import com.iagent.request.parse.ObjectResultParser;
import com.iagent.scan.ClassPathBeanScanner;
import com.iagent.scan.resolver.InterfaceAnnotationResolver;
import com.iagent.scan.resolver.NativeInterfaceAnnotationResolver;
import com.iagent.scan.resolver.annotation.*;
import com.iagent.util.Assert;
import com.iagent.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * http url agent all Configuration
 */
public final class IagentConfiguration {
    //scanner package
    private String [] basePackages;
    //log type
    private Class<? extends Logger> logImpl;
    private Logger logger = LogFactory.getLogger(IagentConfiguration.class);
    //json type
    private Class<? extends JSONSupport> jsonSupport;
    //default Annotation Handler
    private String handlerName = InterfaceAnnotationResolver.DEFAULT_ANNOTATION_NAME;
    // default request config
    private RequestConfig defaultRequestConfig = new RequestConfig(HttpExecutor.DEFAULT_EXECUTOR);
    //interface and proxy object of Map
    private final BeanRegister<Object> iagentRegister = new GenericBeanRegister<>(16);
    //HttpExecutor Map Data
    private final BeanRegister<HttpExecutor> executorRegister = new GenericBeanRegister<>(8);
    //method and IagentBeanWrapper of method Map
    private final BeanRegister<IagentBeanWrapper> beanWrapperRegister = new GenericBeanRegister<>(32);
    //is complete initialize
    private final AtomicBoolean initialize = new AtomicBoolean(false);
    //register alias data
    private final AliasNameRegister<Class> aliasRegister = new AliasNameRegister<>(16);
    // proxy 处理器
    private final IagentProxyHandler proxyHandle = new IagentProxyHandler(this);
    // proxy class 集合
    private final List<Class> proxyClassList = new ArrayList<>(16);

    {
        // 日志适配器
        aliasRegister.registerAlias("SLF4J", Slf4jImpl.class);
        aliasRegister.registerAlias("COMMONS_LOGGING", JakartaCommonsLoggingImpl.class);
        aliasRegister.registerAlias("LOG4J", Log4jImpl.class);
        aliasRegister.registerAlias("LOG4J2", Log4j2Impl.class);
        aliasRegister.registerAlias("JDK_LOGGING", Jdk14LoggingImpl.class);
        aliasRegister.registerAlias("STDOUT_LOGGING", ConsoleImpl.class);
        aliasRegister.registerAlias("NO_LOGGING", NoLoggingImpl.class);
        // JSON适配器
        aliasRegister.registerAlias("FASTJSON", FastJsonSupport.class);
        aliasRegister.registerAlias("GSON", GsonSupport.class);
        aliasRegister.registerAlias("JACKSON", JacksonSupport.class);
        // 注解解析器
        aliasRegister.registerAlias("Native", NativeInterfaceAnnotationResolver.class);
        // 参数解析器
        aliasRegister.registerAlias("PathKeyParameterResolver", PathKeyParameterResolver.class);
        aliasRegister.registerAlias("GenericParameterResolver", GenericParameterResolver.class);
        aliasRegister.registerAlias("HeaderParameterResolver", HeaderParameterResolver.class);
        aliasRegister.registerAlias("BodyParameterResolver", BodyParameterResolver.class);
        // 结果解析器
        aliasRegister.registerAlias("ObjectResultParser", ObjectResultParser.class);
        aliasRegister.registerAlias("BinaryResultParser", BinaryResultParser.class);
        aliasRegister.registerAlias("NumberResultParser", NumberResultParser.class);
    }

    public IagentConfiguration(){
        super();
    }

    /**
     * set base packages
     * @param basePackages
     */
    public IagentConfiguration(String[] basePackages){
        this.basePackages = basePackages;
        init();
    }

    public IagentConfiguration(String basePackages){
        this.basePackages = new String[]{basePackages};
        init();
    }

    public void setBasePackages(String [] basePackages) {
        this.basePackages = basePackages;
    }

    /**
     * set log type
     * @param logImpl
     */
    public void setLogImpl(Class<? extends Logger> logImpl) {
        if(logImpl != null) {
            this.logImpl = logImpl;
            LogFactory.useLogging(this.logImpl);
            logger = LogFactory.getLogger(this.getClass());
        }
    }

    /**
     * set log type
     * @param logImpl
     */
    public void setLogImpl(String logImpl) {
        if(logImpl != null) {
            this.logImpl = (Class<? extends Logger>) aliasRegister.getAlias(logImpl);
            LogFactory.useLogging(this.logImpl);
            logger = LogFactory.getLogger(this.getClass());
        }
    }

    /**
     * set json type
     * @param jsonSupport
     */
    public void setJsonSupport(Class<? extends JSONSupport> jsonSupport) {
        if(jsonSupport != null) {
            this.jsonSupport = jsonSupport;
            JSON.useJson(this.jsonSupport);
        }
    }

    /**
     * set json type
     * @param jsonSupport
     */
    public void setJsonSupport(String jsonSupport) {
        if(jsonSupport != null) {
            this.jsonSupport = (Class<? extends JSONSupport>) aliasRegister.getAlias(jsonSupport);
            JSON.useJson(this.jsonSupport);
        }
    }
    /**
     * initialize package and scanner package
     */
    public void init(){
        Assert.notNull(basePackages, "Iagent base packages is null!");
        try {
            if(!initialize.compareAndSet(false, true)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(" Iagent Scanner Class path is running");
                }
                return;
            }
            long startTime = System.nanoTime();
            ClassPathBeanScanner scanner = new ClassPathBeanScanner(this);
            scanner.scannerPackages(this.iagentRegister, this.basePackages, this.proxyClassList);
            logger.info("Iagent Load Success, Take Time [" + 1.0 * (System.nanoTime() - startTime)/1000000 + "] ms, the number of instance is " + this.iagentRegister.size());
        } catch (Throwable t) {
            logger.error("Error Create Iagent Interface Proxy", t);
            throw new IllegalArgumentException("Error Create Iagent Interface Proxy");
        }
    }

    /**
     * get proxy object by interface class
     * @param cls
     * @param <T>
     * @return
     */
    protected <T> T getIagentBean(Class<T> cls){
        return this.iagentRegister.getBeanByClass(ClassUtils.getClassPathByClass(cls), cls);
    }

    public String getHandlerName() {
        return handlerName;
    }

    public RequestConfig getDefaultRequestConfig() {
        return defaultRequestConfig;
    }

    public void setDefaultRequestConfig(RequestConfig defaultRequestConfig) {
        this.defaultRequestConfig = defaultRequestConfig;
    }

    /**
     * 拿到所有别名注解起
     * @return
     */
    public AliasNameRegister<Class> getAliasRegister() {
        return aliasRegister;
    }

    /**
     * 获取所有的执行器实例
     * @return
     */
    public BeanRegister<HttpExecutor> getExecutorRegister() {
        return executorRegister;
    }

    /**
     * 获取所有method 实例
     * @return
     */
    public BeanRegister<IagentBeanWrapper> getBeanWrapperRegister() {
        return beanWrapperRegister;
    }

    /**
     * 设置注解处理器
     * @param name
     */
    public void setHandlerName(String name) {
        if (aliasRegister.containBeanName(name) &&
                InterfaceAnnotationResolver.class.isAssignableFrom(aliasRegister.getBeanObject(name))) {
            this.handlerName = name;
        } else {
            this.handlerName = InterfaceAnnotationResolver.DEFAULT_ANNOTATION_NAME;
        }
    }

    public IagentProxyHandler getProxyHandle() {
        return proxyHandle;
    }

    public List<Class> getProxyClassList() {
        return proxyClassList;
    }

    public String[] getBasePackages() {
        return basePackages;
    }
}
