package com.iagent.scan.resolver.annotation;

import com.iagent.bean.IagentParamBean;

import java.lang.reflect.Parameter;

/**
 * @author liujieyu
 * @date 2022/5/31 10:45
 * @desciption 参数解析器
 */
public interface ParameterResolver {

    /**
     * 是否能解析
     * @param parameter
     * @return
     */
    boolean isResolver(Parameter parameter);

    /**
     * 参数处理封装
     * @param parameter
     * @param builder
     */
    void parameterHandle(Parameter parameter, IagentParamBean.IagentParamBeanBuilder builder, int index);

    /**
     * 请求前封装参数
     * @param args
     */
    Object requestHandle(Object... args) throws Exception;
}
