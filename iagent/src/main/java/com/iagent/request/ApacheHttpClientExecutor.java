package com.iagent.request;

import com.iagent.annotation.Order;
import com.iagent.bean.IagentBeanWrapper;
import com.iagent.config.IagentConfiguration;
import com.iagent.constant.HttpConstant;
import com.iagent.constant.HttpEnum;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.request.parse.BinaryResultParser;
import com.iagent.request.parse.NumberResultParser;
import com.iagent.request.parse.ObjectResultParser;
import com.iagent.request.parse.ResultParser;
import com.iagent.scan.resolver.annotation.*;
import com.iagent.util.CollectionUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liujieyu
 * @date 2022/6/10 20:03
 * @desciption 使用 Apache Http Client 作为执行器底层
 */
public class ApacheHttpClientExecutor extends AbstractHttpExecutor {

    private static final Logger logger = LogFactory.getLogger(ApacheHttpClientExecutor.class);

    private PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager();

    /**
     * 记录时间相同的org.apache.http.client.config.RequestConfig
     */
    private ConcurrentHashMap<String, RequestConfig> configHashMap = new ConcurrentHashMap<>(16);

    {
        clientConnectionManager.setMaxTotal(50);
        clientConnectionManager.setDefaultMaxPerRoute(10);
    }

    public ApacheHttpClientExecutor(IagentConfiguration configuration) {
        super(configuration);
    }

    public CloseableHttpClient getHttpClientByPool(int connectionTime, int readTime) {
        // 这种写法很暴力，后面在优化一下
        String key = String.valueOf(connectionTime) + String.valueOf(readTime);
        if (configHashMap.contains(key)) {
            return HttpClients.custom().setConnectionManager(clientConnectionManager).setDefaultRequestConfig(configHashMap.get(key)).build();
        }
        RequestConfig requestConfig = configHashMap.put(key, RequestConfig.custom().setConnectionRequestTimeout(200).setConnectTimeout(connectionTime).setSocketTimeout(readTime).build());
        return HttpClients.custom().setConnectionManager(clientConnectionManager).setDefaultRequestConfig(requestConfig).build();
    }

    @Override
    public Object sendHttp(IagentBeanWrapper wrapper, Object[] args) throws Exception {
        CloseableHttpClient httpClientByPool = getHttpClientByPool(wrapper.getBean().getConnectionTime(), wrapper.getBean().getReadTime());

        // 请求类型 Content-Type
        String contentType = wrapper.getBean().getContentType();
        // 请求的真实 http 地址
        String url = wrapper.getBean().getUrl();

        try {
            HttpRequestBase httpRequest = null;
            if (wrapper.getParamBean().getPathIndex() == null || wrapper.getParamBean().getPathIndex().isEmpty()) {
                // 说明路径上没有参数
                httpRequest = getHttpRequest(wrapper.getBean().getRequestType(), url, wrapper.getParamBean().getParamIndex(), args, contentType);
            } else {
                for (ParameterResolver parameterResolver : getParameterResolvers()) {
                    // 处理url路径参数
                    if (parameterResolver instanceof PathKeyParameterResolver && wrapper.getParamBean().getPathIndex() != null) {
                        url = (String) parameterResolver.requestHandle(wrapper.getParamBean().getPathIndex(), url, args);
                        // 创建执行对象
                        httpRequest = getHttpRequest(wrapper.getBean().getRequestType(), url, wrapper.getParamBean().getParamIndex(), args, contentType);
                        break;
                    }
                }
            }
            // 使用默认 参数解析器 进行处理
            for (ParameterResolver parameterResolver : getParameterResolvers()) {
                // 请求头参数处理
                if (parameterResolver instanceof HeaderParameterResolver && wrapper.getParamBean().getHeaderIndex() != null) {
                    parameterResolver.requestHandle(wrapper.getParamBean().getHeaderIndex(), httpRequest, args);
                }
                // 处理body里面的参数
                if (parameterResolver instanceof BodyParameterResolver && wrapper.getParamBean().getBodyIndex() != null) {
                    parameterResolver.requestHandle(httpRequest, args[wrapper.getParamBean().getBodyIndex().intValue()], contentType);
                }
                // 处理get url参数，表单参数 处理POST和PUT请求即可
                if (parameterResolver instanceof GenericParameterResolver) {
                    parameterResolver.requestHandle(httpRequest, args, wrapper.getParamBean(), contentType);
                }
            }

            CloseableHttpResponse execute = httpClientByPool.execute(httpRequest);

            boolean existParser = false;
            //使用结果集解析器解析
            for (ResultParser resultParser : getResultParsers()) {
                if (resultParser.isParser(wrapper.getReturnClassType())) {
                    return resultParser.parseResult(wrapper.getReturnClassType(), execute);
                }
            }

            if (!existParser) {
                throw new IllegalArgumentException("not found execute result parser, the return type is [" + wrapper.getReturnClassType() + "]");
            }
        } catch (Throwable e) {
            logger.error("The Executor send Http Request Error!!!", e);
            throw new RuntimeException("The Executor send Http Request Error!!!");
        }

        return null;
    }

    /**
     * 获取到http 请求，优先处理GET和DELETE请求中的参数，跟在URL上面
     *
     * @param httpEnum
     * @param url
     * @return
     */
    private HttpRequestBase getHttpRequest(HttpEnum httpEnum, String url, Map<String, Integer> paramsIndex,
                                           Object[] args, String contentType) {
        HttpRequestBase httpRequestBase;
        switch (httpEnum) {
            case POST:
                if (HttpConstant.X_WWW_FORM_URLENCODED.equals(contentType)) {
                    url = handleUrl(paramsIndex, args, url);
                }
                httpRequestBase = new HttpPost(url);
                break;
            case PUT:
                if (HttpConstant.X_WWW_FORM_URLENCODED.equals(contentType)) {
                    url = handleUrl(paramsIndex, args, url);
                }
                httpRequestBase = new HttpPut(url);
                break;
            case DELETE:
                url = handleUrl(paramsIndex, args, url);
                httpRequestBase = new HttpDelete(url);
                break;
            case GET:
                // 是默认的请求方式一致
            default:
                // 默认使用 Get 请求
                url = handleUrl(paramsIndex, args, url);
                httpRequestBase = new HttpGet(url);
        }

        return httpRequestBase;
    }

    /**
     * 将路径参数封装到url上
     *
     * @param paramsIndex
     * @param args
     * @return
     */
    private String handleUrl(Map<String, Integer> paramsIndex, Object[] args, String url) {
        StringBuilder stringBuilder = null;
        if (paramsIndex != null && !paramsIndex.isEmpty()) {
            stringBuilder = new StringBuilder();
            for (Map.Entry<String, Integer> entry : paramsIndex.entrySet()) {
                String name = entry.getKey();
                String value = String.valueOf(args[entry.getValue().intValue()]);
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(name);
                stringBuilder.append("=");
                stringBuilder.append(value);
            }
        }
        if (null == stringBuilder) {
            return url;
        } else {
            String params = stringBuilder.toString();
            return url + "?" + params;
        }
    }
}
