package com.iagent.spring.boot;

import com.iagent.request.RequestConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iagent")
public class IagentProperties {

    private String logImpl;

    private String jsonSupport;

    private RequestConfig requestConfig;

    public String getLogImpl() {
        return logImpl;
    }

    public void setLogImpl(String logImpl) {
        this.logImpl = logImpl;
    }

    public String getJsonSupport() {
        return jsonSupport;
    }

    public void setJsonSupport(String jsonSupport) {
        this.jsonSupport = jsonSupport;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }
}
