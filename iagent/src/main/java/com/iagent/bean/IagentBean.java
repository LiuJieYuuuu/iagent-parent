package com.iagent.bean;

import com.iagent.constant.HttpConstant;
import com.iagent.constant.HttpEnum;
import com.iagent.request.RequestConfig;
import com.iagent.util.StringUtils;

import java.io.Serializable;

/**
 * <p>http uri information loading to HttpUriBean object,
 * support deep clone and serializable,use to Builder Pattern to create object
 * </p>
 */
public class IagentBean implements Cloneable,Serializable {

    /**
     * http request url
     */
    private String url;

    /**
     * request type
     *
     * @see HttpEnum
     */
    private HttpEnum requestType;

    /**
     * Content-Type
     *
     * @see HttpConstant
     */
    private String contentType;

    /**
     * conection http server time
     */
    private int connectionTime;

    /**
     * read from http server time
     */
    private int readTime;

    /**
     *
     * @param builder iagent bean builder
     */
    private IagentBean(IagentBeanBuilder builder) {
        super();
        this.url = builder.getUrl();
        this.connectionTime = builder.getConnectionTime();
        this.contentType = builder.getContentType();
        this.readTime = builder.getReadTime();
        this.requestType = builder.getRequestType();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    @Override
    public IagentBean clone() {
        try {
            return (IagentBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <b>IagentBean Builder</b>
     */
    public static class IagentBeanBuilder{

        private String url;

        private HttpEnum requestType;

        private String contentType;

        private int connectionTime;

        private int readTime;

        private RequestConfig requestConfig;

        public IagentBeanBuilder(RequestConfig requestConfig){
            super();
            this.requestConfig = requestConfig;
        }

        public static IagentBeanBuilder create(RequestConfig requestConfig){
            return new IagentBeanBuilder(requestConfig);
        }

        public IagentBeanBuilder url(String url){
            this.url = url;
            return this;
        }

        public IagentBeanBuilder requestType(HttpEnum[] requestType){
            if (requestType == null || requestType.length == 0) {
                this.requestType = requestConfig.getRequestType();
            } else {
                this.requestType = requestType[0];
            }
            return this;
        }

        public IagentBeanBuilder contentType(String contentType){
            if (StringUtils.isNotEmpty(contentType)) {
                this.contentType = contentType;
            } else {
                this.contentType = requestConfig.getContentType();
            }
            return this;
        }

        public IagentBeanBuilder connectionTime(int connectionTime){
            if (connectionTime <= 0) {
                this.connectionTime = requestConfig.getConnectionTime();
            } else {
                this.connectionTime = connectionTime;
            }
            return this;
        }

        public IagentBeanBuilder readTime(int readTime){
            if (readTime <= 0) {
                this.readTime = requestConfig.getReadTime();
            } else {
                this.readTime = readTime;
            }
            return this;
        }

        public String getUrl() {
            return url;
        }

        public HttpEnum getRequestType() {
            return requestType;
        }

        public String getContentType() {
            return contentType;
        }

        public int getConnectionTime() {
            return connectionTime;
        }

        public int getReadTime() {
            return readTime;
        }

        public IagentBean build(){
            return new IagentBean(this);
        }

    }

}
