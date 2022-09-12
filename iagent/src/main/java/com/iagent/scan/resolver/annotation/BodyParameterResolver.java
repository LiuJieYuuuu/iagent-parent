package com.iagent.scan.resolver.annotation;

import com.iagent.annotation.ParamBody;
import com.iagent.bean.IagentParamBean;
import com.iagent.constant.HttpConstant;
import com.iagent.json.JSON;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.util.Assert;
import com.iagent.util.CollectionUtils;
import com.iagent.util.ReflectUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * request body 参数解析器
 */
public class BodyParameterResolver implements ParameterResolver {

    private static final Logger logger = LogFactory.getLogger(BodyParameterResolver.class);

    @Override
    public boolean isResolver(Parameter parameter) {
        ParamBody annotation = parameter.getAnnotation(ParamBody.class);
        if (annotation == null) {
            return false;
        }
        return true;
    }

    @Override
    public void parameterHandle(Parameter parameter, IagentParamBean.IagentParamBeanBuilder builder, int index) {
        builder.addBodyIndex(index);
    }

    @Override
    public Object requestHandle(Object... args) {
        HttpRequestBase httpRequestBases = (HttpRequestBase) args[0];
        Object bodyObject = args[1];
        String contentType = String.valueOf(args[2]);
        Assert.notNull(bodyObject, "The Body Parameter is Not Null");
        // start handle
        if (HttpConstant.MULTIPART_FORM_DATA_VALUE.equals(contentType)) {
            // 表单类型
            Map<String, Object> objectMap = ReflectUtils.getKeyValueByObject(bodyObject, false);
            List<NameValuePair> list = new ArrayList<>();
            for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
                list.add(basicNameValuePair);
            }
            try {
                if (CollectionUtils.isNotEmpty(list)) {
                    ((HttpEntityEnclosingRequestBase) httpRequestBases).setEntity(new UrlEncodedFormEntity(list));
                }
            } catch (UnsupportedEncodingException e) {
                logger.error("Unsupported Encoding Error Info:" + e.getMessage(), e);
                throw new IllegalArgumentException("Unsupported Encoding Error Info:" + e.getMessage());
            }
        } else {
            // 非表单类型
            if (httpRequestBases instanceof HttpPost) {
                // HttpPost 对象
                String body = JSON.toJSONString(bodyObject);
                StringEntity bodyEntity = new StringEntity(body, ContentType.create(contentType, Consts.UTF_8));
                ((HttpPost) httpRequestBases).setEntity(bodyEntity);

            } else if (httpRequestBases instanceof HttpPut) {
                // HttpPut 对象
                String body = JSON.toJSONString(bodyObject);
                StringEntity bodyEntity = new StringEntity(body, ContentType.create(contentType, Consts.UTF_8));//ContentType.APPLICATION_JSON
                ((HttpPut) httpRequestBases).setEntity(bodyEntity);
            } else {
                throw new IllegalArgumentException("Request Type is Error, Body parameter must be Post or Put!");
            }
        }

        return null;
    }
}
