package com.iagent.scan.resolver;

import com.iagent.bean.IagentBean;
import com.iagent.bean.IagentBeanWrapper;
import com.iagent.register.BeanRegister;
import com.iagent.request.HttpExecutor;

import java.lang.reflect.Method;

/**
 * @author liujieyu
 * @date 2022/5/25 19:59
 * @desciption
 */
public interface InterfaceAnnotationResolver {

    /**
     * 默认使用原生接口注解解析器
     */
    String DEFAULT_ANNOTATION_NAME = "Native";

    /**
     * 处理接口上注解信息
     * @param method
     */
    void handlerInterfaceMethod(IagentBean pBean, Method method);

    /**
     * 处理类上注解
     * @param tClass
     * @return
     */
    IagentBean handlerClassIagentBean(Class<?> tClass);

    /**
     * 函数上是否存在注解
     * @param method
     * @return
     */
    boolean existsIagentAnnotation(Method method);
}
