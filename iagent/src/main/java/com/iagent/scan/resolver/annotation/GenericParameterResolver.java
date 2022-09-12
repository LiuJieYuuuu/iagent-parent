package com.iagent.scan.resolver.annotation;

import com.iagent.annotation.ParamKey;
import com.iagent.bean.IagentParamBean;
import com.iagent.constant.HttpConstant;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通用参数Key 解析器
 */
public class GenericParameterResolver implements ParameterResolver {
	
	private static final Logger logger = LogFactory.getLogger(GenericParameterResolver.class);
	
    @Override
    public boolean isResolver(Parameter parameter) {
        ParamKey annotation = parameter.getAnnotation(ParamKey.class);
        if (annotation == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void parameterHandle(Parameter parameter, IagentParamBean.IagentParamBeanBuilder builder, int index) {
        ParamKey annotation = parameter.getAnnotation(ParamKey.class);
        String name = annotation.value();
        if (isInputStreamParam(parameter)) {
            //文件流传输
            builder.addInputStreamIndex(name, index);
        } else {
            //普通参数传输
            builder.addParamIndex(name, index);
        }
    }

    @Override
    public Object requestHandle(Object... args) {
        HttpRequestBase httpRequest = (HttpRequestBase) args[0];
        Object[] params = (Object[]) args[1];
        IagentParamBean paramBean = (IagentParamBean) args[2];
        String contentType = String.valueOf(args[3]);
        if (httpRequest instanceof HttpPost || httpRequest instanceof HttpPut) {
            HttpEntity httpEntity = null;
            Map<String, Integer> inputStreamIndex = paramBean.getInputStreamIndex();
            Map<String, Integer> paramIndex = paramBean.getParamIndex();
            if (inputStreamIndex == null || inputStreamIndex.isEmpty()) {
                // 没有文件参数，即普通表单参数提交即可
                if (paramIndex != null && !paramIndex.isEmpty() && HttpConstant.MULTIPART_FORM_DATA_VALUE.equals(contentType)) {
                    List<NameValuePair> list = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : paramIndex.entrySet()) {
                        String key = entry.getKey();
                        Object value = params[entry.getValue().intValue()];
                        BasicNameValuePair basicNameValuePair = new BasicNameValuePair(key, String.valueOf(value));
                        list.add(basicNameValuePair);
                    }
                    try {
                        httpEntity = new UrlEncodedFormEntity(list);
                    } catch (UnsupportedEncodingException e) {
                    	logger.error("Unsupported Encoding Error Info:" + e.getMessage(), e);
                        throw new IllegalArgumentException("Unsupported Encoding Error Info:" + e.getMessage());
                    }
                }
            } else {
                // 使用表单提交
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                // 普通参数添加
                if (paramIndex != null && !paramIndex.isEmpty() && HttpConstant.MULTIPART_FORM_DATA_VALUE.equals(contentType)) {
                    for (Map.Entry<String, Integer> entry : paramIndex.entrySet()) {
                        String key = entry.getKey();
                        Object value = params[entry.getValue().intValue()];
                        multipartEntityBuilder.addTextBody(key, String.valueOf(value), ContentType.create("text/plain", Charset.forName("UTF-8")));
                    }
                }
                // 文件类型
                for (Map.Entry<String, Integer> entry : inputStreamIndex.entrySet()) {
                    String key = entry.getKey();
                    Object value = params[entry.getValue().intValue()];
                    if (value instanceof File) {
                        multipartEntityBuilder.addBinaryBody(key, (File) value);
                    }
                    if (value instanceof InputStream) {
                        multipartEntityBuilder.addBinaryBody(key, (InputStream) value);
                    }
                }
                // 防止乱码
                multipartEntityBuilder.setCharset(StandardCharsets.UTF_8);
                multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                httpEntity = multipartEntityBuilder.build();
            }

            if (httpEntity != null) {
                ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(httpEntity);
            }
        }

        return null;
    }

    /**
     * 校验参数是否为文件形式
     * @param parameter
     * @return
     */
    private boolean isInputStreamParam(Parameter parameter) {
        Class<?> type = parameter.getType();
        if (File.class.isAssignableFrom(type) || InputStream.class.isAssignableFrom(type)) {
            return true ;
        } else {
            return false;
        }
    }

}
