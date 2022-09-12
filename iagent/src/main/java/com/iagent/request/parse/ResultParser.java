package com.iagent.request.parse;

import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * 返回结果解析
 */
public interface ResultParser {

    /**
     * 能否解析该返回结果集
     * @param cls
     * @return
     */
    boolean isParser(Class<?> cls);

    /**
     * 将结果集解析CloseableHttpResponse成执行类型对象即可
     * @param <T>
     * @param returnClassType
     * @param closeableHttpResponse
     * @return
     */
    <T> Object parseResult(Class<T> returnClassType, CloseableHttpResponse closeableHttpResponse) throws Exception;

    /**
     * 通用方法， 判断HTTP接口返回状态是否OK
     * @param closeableHttpResponse
     * @return
     */
    default boolean isOk(CloseableHttpResponse closeableHttpResponse) {
        return closeableHttpResponse.getStatusLine().getStatusCode() == 200;
    }
}
