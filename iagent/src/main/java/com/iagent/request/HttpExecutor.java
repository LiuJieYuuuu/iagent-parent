package com.iagent.request;

import com.iagent.bean.IagentBeanWrapper;

/**
 * <b>Common Http Request Interface</b>
 */
public interface HttpExecutor {

    /**
     * default executor
     *
     */
    Class<? extends HttpExecutor> DEFAULT_EXECUTOR = ApacheHttpClientExecutor.class;

    /**
     * specific http request
     * @param bean
     * @param args
     * @return
     */
    Object sendHttp(IagentBeanWrapper bean, Object[] args) throws Exception;

}
