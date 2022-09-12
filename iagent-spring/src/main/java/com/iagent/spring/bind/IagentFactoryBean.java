package com.iagent.spring.bind;

import com.iagent.config.AbstractIagentFactory;
import com.iagent.spring.IagentProxyBean;
import org.springframework.beans.factory.FactoryBean;

/**
 * <b>spring integration use FactoryBean<T></></b>
 * @param <T>
 */
public class IagentFactoryBean<T> implements FactoryBean<T> {

    /**
     * Spring 参数封装类
     */
    private IagentProxyBean iagentProxyBean;

    /**
     * iagent 工厂
     */
    private AbstractIagentFactory iagentFactory;

    /**
     * 接口Class对象
     */
    private Class<T> IClass;

    private String[] basePackages;

    public IagentFactoryBean() {
        super();
    }

    public IagentFactoryBean(Class<T> IClass){
        this.IClass = IClass;
    }

    public void setIagentFactory(AbstractIagentFactory iagentFactory) {
        this.iagentFactory = iagentFactory;
    }

    public void setIagentProxyBean(IagentProxyBean iagentProxyBean) {
        this.iagentProxyBean = iagentProxyBean;
    }

    public void setIClass(Class<T> IClass) {
        this.IClass = IClass;
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }

    @Override
    public T getObject() throws Exception {
        if (this.iagentFactory == null) {
            this.iagentFactory = this.iagentProxyBean.getIagentFactory();
            if (this.iagentFactory == null) {
                this.iagentProxyBean.setBasePackages(basePackages);
                this.iagentProxyBean.afterPropertiesSet();
                this.iagentFactory = this.iagentProxyBean.getIagentFactory();
            }
        }
        return iagentFactory.getProxy(IClass);
    }

    @Override
    public Class<?> getObjectType() {
        return IClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
