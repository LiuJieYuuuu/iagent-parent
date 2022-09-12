package com.iagent.config;

/**
 * @author liujieyu
 * @date 2022/6/22 16:21
 * @desciption 默认获取代理实例工厂类
 */
public class DefaultIagentFactory extends AbstractIagentFactory {

    public DefaultIagentFactory() {
        super();
    }

    public DefaultIagentFactory(IagentConfiguration configuration) {
        super(configuration);
    }

    public DefaultIagentFactory(String[] basePackages) {
        super(basePackages);
    }

    @Override
    public <T> T getProxy(Class<T> clazz) throws UnsupportedOperationException {
        return super.getConfiguration().getIagentBean(clazz);
    }
}
