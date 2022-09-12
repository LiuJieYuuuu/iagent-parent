package com.iagent.scan.resolver.annotation;

import com.iagent.annotation.ParamHeader;
import com.iagent.bean.IagentParamBean;
import com.iagent.util.Assert;
import org.apache.http.client.methods.HttpRequestBase;

import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 请求头解析
 */
public class HeaderParameterResolver implements ParameterResolver {
    @Override
    public boolean isResolver(Parameter parameter) {
        ParamHeader annotation = parameter.getAnnotation(ParamHeader.class);
        if (annotation == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void parameterHandle(Parameter parameter, IagentParamBean.IagentParamBeanBuilder builder, int index) {
        ParamHeader annotation = parameter.getAnnotation(ParamHeader.class);
        String name = annotation.value();
        builder.addHeaderIndex(name, index);
    }

    @Override
    public Object requestHandle(Object... args) {
        Map<String, Integer> headerMap = (Map<String, Integer>) args[0];
        HttpRequestBase httpRequestBases = (HttpRequestBase) args[1];
        Object[] params = (Object[]) args[2];

        Assert.notNull(headerMap, "The Header Map is Not Null!");
        for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
            String headerName = entry.getKey();
            httpRequestBases.setHeader(headerName, String.valueOf(params[entry.getValue().intValue()]));
        }

        return null;
    }
}
