package com.iagent.bean;

import com.iagent.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liujieyu
 * @date 2022/5/30 20:03
 * @desciption 参数集合
 */
public class IagentParamBean {

    /**
     * the parameter of Array on the method
     */
    private Class<?>[] parametersClass;

    /**
     * http url parameters index
     */
    private Map<String, Integer> paramIndex;

    /**
     * key of the url path, and args of method'annotations
     * eg: http://localhost:80/{param}
     * like Spring MVC @PathVariable("param")
     */
    private Map<String,Integer> pathIndex;

    /**
     * request header parameter's index
     */
    private Map<String, Integer> headerIndex;

    /**
     * http request body data's index
     */
    private Integer bodyIndex;

    /**
     * file or input stream's index
     */
    private Map<String, Integer> inputStreamIndex;

    private IagentParamBean() {
        super();;
    }

    public Class<?>[] getParametersClass() {
        return parametersClass;
    }

    public void setParametersClass(Class<?>[] parametersClass) {
        this.parametersClass = parametersClass;
    }

    public Map<String, Integer> getParamIndex() {
        return paramIndex;
    }

    public void setParamIndex(Map<String, Integer> paramIndex) {
        this.paramIndex = paramIndex;
    }

    public Map<String, Integer> getPathIndex() {
        return pathIndex;
    }

    public void setPathIndex(Map<String, Integer> pathIndex) {
        this.pathIndex = pathIndex;
    }

    public Map<String, Integer> getHeaderIndex() {
        return headerIndex;
    }

    public void setHeaderIndex(Map<String, Integer> headerIndex) {
        this.headerIndex = headerIndex;
    }

    public Integer getBodyIndex() {
        return bodyIndex;
    }

    public void setBodyIndex(Integer bodyIndex) {
        this.bodyIndex = bodyIndex;
    }

    public Map<String, Integer> getInputStreamIndex() {
        return inputStreamIndex;
    }

    public void setInputStreamIndex(Map<String, Integer> inputStreamIndex) {
        this.inputStreamIndex = inputStreamIndex;
    }

    /**
     * <b>参数建造类</b>
     */
    public static class IagentParamBeanBuilder {

        private Class<?>[] parametersClass;

        private Map<String, Integer> paramIndex;

        private Map<String,Integer> pathIndex;

        private Map<String, Integer> headerIndex;

        private Integer bodyIndex;

        private Map<String, Integer> inputStreamIndex;

        private IagentParamBeanBuilder() {
            super();
        }

        public static IagentParamBeanBuilder create() {
            return new IagentParamBeanBuilder();
        }

        public IagentParamBeanBuilder setParameterClass(Class<?>[] parameterClass) {
            this.parametersClass = parameterClass;
            return this;
        }

        public IagentParamBeanBuilder addParamIndex(String name, Integer index) {
            if (this.paramIndex == null) {
                this.paramIndex = new HashMap<>(4);
                this.paramIndex.put(name, index);
            } else {
                this.paramIndex.put(name, index);
            }
            return this;
        }

        public IagentParamBeanBuilder addPathIndex(String name, Integer index) {
            if (this.pathIndex == null) {
                this.pathIndex = new HashMap<>(4);
                this.pathIndex.put(name, index);
            } else {
                this.pathIndex.put(name, index);
            }

            return this;
        }

        public IagentParamBeanBuilder addHeaderIndex(String name, Integer index) {
            if (this.headerIndex == null) {
                this.headerIndex = new HashMap<>(4);
                this.headerIndex.put(name, index);
            } else {
                this.headerIndex.put(name, index);
            }

            return this;
        }

        public IagentParamBeanBuilder addBodyIndex(Integer index) {
            if (this.bodyIndex == null) {
                this.bodyIndex = index;
            } else {
                throw new IllegalArgumentException("This Body Parameter is Too Many!");
            }

            return this;
        }

        public IagentParamBeanBuilder addInputStreamIndex(String name, Integer index) {
            if (this.inputStreamIndex == null) {
                this.inputStreamIndex = new HashMap<>(4);
                this.inputStreamIndex.put(name, index);
            } else {
                this.inputStreamIndex.put(name, index);
            }

            return this;
        }

        public IagentParamBean build() {
            IagentParamBean paramBean = new IagentParamBean();
            paramBean.setBodyIndex(this.bodyIndex);
            paramBean.setHeaderIndex(this.headerIndex);
            paramBean.setInputStreamIndex(this.inputStreamIndex);
            paramBean.setParamIndex(this.paramIndex);
            paramBean.setPathIndex(this.pathIndex);
            return paramBean;
        }
    }
}
