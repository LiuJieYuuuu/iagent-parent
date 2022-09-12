package com.iagent.scan.resolver.annotation;

import com.iagent.config.IagentConfiguration;
import com.iagent.util.ClassUtils;
import com.iagent.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数注解解析器
 */
public abstract class AbstractParameterResolver {

    private IagentConfiguration configuration;

    private static final List<ParameterResolver> parameterResolvers = new ArrayList<>(8);

    public AbstractParameterResolver(IagentConfiguration configuration) {
        this.configuration = configuration;
        List<Class<ParameterResolver>> parameterResolverClass = this.configuration.getAliasRegister().getAliasClassListByClass(ParameterResolver.class);
        if (CollectionUtils.isEmpty(parameterResolvers)) {
            for (Class<ParameterResolver> parameterClass : parameterResolverClass) {
                parameterResolvers.add(ClassUtils.newInstance(parameterClass));
            }
        }
    }

    /**
     * 拿到所有参数解析器
     * @return
     */
    public List<ParameterResolver> getParameterResolvers() {
        return parameterResolvers;
    }

}
