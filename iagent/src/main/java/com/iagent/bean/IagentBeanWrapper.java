package com.iagent.bean;

import com.iagent.request.HttpExecutor;

import java.io.Serializable;

/**
 * Method Wrapper
 */
public class IagentBeanWrapper implements Cloneable,Serializable {

    /**
     * 参数信息
     */
    private IagentBean bean;

    /**
     * 选用执行器
     */
    private HttpExecutor executor;

    /**
     * Method 参数
     */
    private IagentParamBean paramBean;

    /**
     * 返回值类型
     */
    private Class<?> returnClassType;

    public IagentBeanWrapper(){
        super();
    }

    public IagentBeanWrapper(IagentBean bean){
        super();
        this.bean = bean;
    }

    public IagentBeanWrapper(IagentBean bean, HttpExecutor executor){
        super();
        this.bean = bean;
        this.executor = executor;
    }

    public IagentBeanWrapper(HttpExecutor executor){
        super();
        this.executor = executor;
    }

    public IagentBean getBean() {
        return bean;
    }

    public void setBean(IagentBean bean) {
        this.bean = bean;
    }

    public HttpExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(HttpExecutor executor) {
        this.executor = executor;
    }

    public IagentParamBean getParamBean() {
        return paramBean;
    }

    public void setParamBean(IagentParamBean paramBean) {
        this.paramBean = paramBean;
    }

    public Class<?> getReturnClassType() {
        return returnClassType;
    }

    public void setReturnClassType(Class<?> returnClassType) {
        this.returnClassType = returnClassType;
    }

}
