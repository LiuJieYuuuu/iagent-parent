package com.iagent.request;

import com.iagent.config.IagentConfiguration;
import com.iagent.request.parse.ResultParser;
import com.iagent.scan.resolver.annotation.AbstractParameterResolver;
import com.iagent.util.ClassUtils;
import com.iagent.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <b> the Abstract Common Http Request Executor</b>
 * extends AbstractParameterResolver.java 拿到默认的请求参数解析器，不一定真的要使用，可以不用
 */
public abstract class AbstractHttpExecutor extends AbstractParameterResolver implements HttpExecutor {

    private IagentConfiguration configuration;

    private List<ResultParser> resultParsers;

    public AbstractHttpExecutor(IagentConfiguration configuration) {
        super(configuration);
        this.configuration = configuration;
        if (null != configuration) {
            initializeResultParser();
        }
    }

    /**
     * 初始化结果解析器
     */
    public void initializeResultParser() {
        List<Class<ResultParser>> resultParserClasses = this.configuration.getAliasRegister()
                .getAliasClassListByClass(ResultParser.class);
        CollectionUtils.sortByOrder(resultParserClasses);
        this.resultParsers = new ArrayList<>(8);
        for (Class<ResultParser> clazz : resultParserClasses) {
            resultParsers.add(ClassUtils.newInstance(clazz));
        }
    }

    /**
     * 设置配置对象
     * @param configuration
     */
    public void setConfiguration(IagentConfiguration configuration) {
        this.configuration = configuration;
        if (null != configuration) {
            initializeResultParser();
        }
    }

    public List<ResultParser> getResultParsers() {
        return resultParsers;
    }

}
