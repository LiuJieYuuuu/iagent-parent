package com.iagent.config;

/**
 * Abstract Iagent Factory
 * 便于后期扩展
 */
public abstract class AbstractIagentFactory {

    private final IagentConfiguration configuration;

    public AbstractIagentFactory() {
        super();
        // 默认扫描所有包下类, 但是不建议扫描所有，容易报错
        configuration = new IagentConfiguration(new String[]{""});
        this.configuration.init();
    }

    public AbstractIagentFactory(String[] basePackages) {
        super();
        configuration = new IagentConfiguration();
        this.configuration.setBasePackages(basePackages);
        this.configuration.init();
    }

    public AbstractIagentFactory(IagentConfiguration configuration) {
        super();
        this.configuration = configuration;
        this.configuration.init();
    }

    public IagentConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * 拿到指定接口得代理类
     * @param clazz
     * @param <T>
     * @return
     * @throws UnsupportedOperationException
     */
    public abstract <T> T getProxy(Class<T> clazz) throws UnsupportedOperationException;

}
