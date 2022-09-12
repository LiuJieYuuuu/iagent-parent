package com.iagent.request;

import com.iagent.constant.HttpConstant;
import com.iagent.constant.HttpEnum;

/**
 * default request configuration
 */
public class RequestConfig {

    private HttpEnum requestType = HttpEnum.GET;

    private String contentType = HttpConstant.X_WWW_FORM_URLENCODED;

    private int connectionTime = HttpConstant.CONNECTION_TIME;

    private int readTime = HttpConstant.READ_TIME;

    private Class<? extends HttpExecutor> httpExecutor = null;

    public RequestConfig() {
        super();
    }

    public RequestConfig(Class<? extends HttpExecutor> httpExecutor) {
        this.httpExecutor = httpExecutor;
    }

    public HttpEnum getRequestType() {
        return requestType;
    }

    public void setRequestType(HttpEnum requestType) {
        this.requestType = requestType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(int connectionTime) {
        this.connectionTime = connectionTime;
    }

    public int getReadTime() {
        return readTime;
    }

    public void setReadTime(int readTime) {
        this.readTime = readTime;
    }

    public Class<? extends HttpExecutor> getHttpExecutor() {
        return httpExecutor;
    }

    public void setHttpExecutor(Class<? extends HttpExecutor> httpExecutor) {
        this.httpExecutor = httpExecutor;
    }
}
