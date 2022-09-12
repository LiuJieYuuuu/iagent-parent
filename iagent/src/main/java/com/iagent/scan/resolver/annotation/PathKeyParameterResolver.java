package com.iagent.scan.resolver.annotation;

import com.iagent.annotation.PathKey;
import com.iagent.bean.IagentParamBean;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.util.Assert;
import com.iagent.util.StringUtils;

import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 路径参数解析器
 */
public class PathKeyParameterResolver implements ParameterResolver {

    private static final Logger logger = LogFactory.getLogger(PathKeyParameterResolver.class);

    @Override
    public boolean isResolver(Parameter parameter) {
        PathKey annotation = parameter.getAnnotation(PathKey.class);
        if (annotation == null) {
            return false;
        }
        return true;
    }

    @Override
    public void parameterHandle(Parameter parameter, IagentParamBean.IagentParamBeanBuilder builder, int index) {
        PathKey annotation = parameter.getAnnotation(PathKey.class);
        String name = annotation.value();
        builder.addPathIndex(name, index);
    }

    @Override
    public Object requestHandle(Object... args) throws Exception {
        Map<String, Integer> pathMap = (Map<String, Integer>) args[0];
        String url = (String) args[1];
        Object[] params = (Object[]) args[2];
        Assert.notNull(pathMap, "The Path Key Map is Not Null!");
        for (Map.Entry<String, Integer> entry : pathMap.entrySet()) {
            String pathKey = entry.getKey();
            url = url.replaceAll("\\{" + pathKey + "\\}", String.valueOf(params[entry.getValue().intValue()]));
        }
        if (url.contains("{") && url.contains("}")) {
            // 如果还存在参数未解析到，则提醒
            logger.warn("The Url has Not resolver parameter [" + StringUtils.getStringByTags(url, "{", "}") + "]");
        }
        return url;
    }
}
