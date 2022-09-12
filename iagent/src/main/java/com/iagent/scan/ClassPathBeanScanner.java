package com.iagent.scan;

import com.iagent.config.IagentConfiguration;
import com.iagent.bean.IagentBean;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.proxy.IagentProxyFactory;
import com.iagent.register.BeanRegister;
import com.iagent.scan.kernel.AbstractResourceScanner;
import com.iagent.scan.resolver.AbstractInterfaceAnnotationResolver;
import com.iagent.scan.resolver.InterfaceAnnotationResolver;
import com.iagent.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <b>interface is the must scanner,it's to be IagentProxyFactory and UriProxy</b>
 *
 * @see com.iagent.proxy.IagentProxyFactory
 * @see com.iagent.proxy.IagentProxy
 */
public final class ClassPathBeanScanner extends AbstractResourceScanner {

    private static final Logger logger = LogFactory.getLogger(ClassPathBeanScanner.class);

    /**
     * <b>use IagentProxyFactory to create proxy factory class</b>
     */
    private IagentProxyFactory proxyFactory;

    /**
     * <b>global configuration</b>
     */
    private IagentConfiguration configuration;

    /**
     * 注解处理器
     */
    private InterfaceAnnotationResolver annotationHandlers;

    /**
     * <b>this is global Configuration's Constructor</b>
     * @param configuration
     */
    public ClassPathBeanScanner(IagentConfiguration configuration){
        super();
        this.configuration = configuration;
        proxyFactory = new IagentProxyFactory(configuration);
        Class<AbstractInterfaceAnnotationResolver> alias = this.configuration.getAliasRegister().getAlias(this.configuration.getHandlerName());
        if (AbstractInterfaceAnnotationResolver.class.isAssignableFrom(alias)) {
            annotationHandlers = ClassUtils.newInstance(alias, new Class[]{IagentConfiguration.class}, new Object[]{this.configuration});
        }
    }

    protected InterfaceAnnotationResolver getPresentAnnotationHandlers() {
        return annotationHandlers;
    }

    /**
     * <b>scanner interface of base packages ,
     * and load to wrapper map </b>
     * @param iagentRegister
     * @param basePackages
     */
    public void scannerPackages(BeanRegister<Object> iagentRegister, String[] basePackages, List<Class> proxyClassList) {
        for(String packages : basePackages){
            //拿到所有符合条件下的Class对象
            Set<Class> allClasses = this.findAllClassByClassPath(packages);
            for (Class aClass : allClasses) {
                //将当前CLass的代理实例注册到代理实例容器中
                String classKey = ClassUtils.getClassPathByClass(aClass);
                iagentRegister.registerBean(classKey, proxyFactory.newInstance(aClass));
                if (logger.isDebugEnabled()) {
                    logger.debug("load class name [" + classKey + "] the instance is " + iagentRegister.getBeanObject(classKey));
                }
                //将每个接口方法注册到包装类里面,以及将执行器注册到执行器容器里面
                registerMethod(aClass);

                // 保存所有扫描出来的Class有序集合
                proxyClassList.add(aClass);
            }

        }
    }

    /**
     * <b>handle annotation of interface is loading to IagentBeanWrapper </b>
     * @param cls
     * @param <T>
     */
    private <T> void registerMethod(Class<T> cls){
        InterfaceAnnotationResolver presentAnnotationHandlers = getPresentAnnotationHandlers();
        //处理类上注解，拿到统一基本信息
        IagentBean iagentBean = presentAnnotationHandlers.handlerClassIagentBean(cls);
        //处理每个接口上的注解
        for (Method method : getDeclaredMethods(cls)) {
            if (presentAnnotationHandlers.existsIagentAnnotation(method)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("the class [" + cls + "] has handled the request method [" + ClassUtils.getClassPathByMethod(method) + "]");
                }
                presentAnnotationHandlers.handlerInterfaceMethod(iagentBean, method);
            }
        }

    }

    /**
     * 获取Class的所有方法
     * @param cls
     * @return
     */
    private Method[] getDeclaredMethods(Class<?> cls) {
        return cls.getMethods();
    }
}
