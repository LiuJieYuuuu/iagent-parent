package com.iagent.request.parse;

import com.iagent.annotation.Order;
import com.iagent.constant.HttpConstant;
import com.iagent.json.JSON;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 对象解析器
 */
@Order(HttpConstant.MAX_ORDER)
public class ObjectResultParser implements ResultParser {
    @Override
    public boolean isParser(Class<?> cls) {
        if (Object.class.isAssignableFrom(cls)) {
            return true;
        }
        return false;
    }

    @Override
    public <T> T parseResult(Class<T> returnClassType, CloseableHttpResponse closeableHttpResponse) throws Exception {
        if (isOk(closeableHttpResponse)) {
            // 成功
            if (String.class.isAssignableFrom(returnClassType)) {
                String resultStr = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
                return (T) resultStr;
            } else {
                String jsonResult = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
                return JSON.getJSONObject(jsonResult, returnClassType);
            }
        } else {
            // 失败
            throw new IllegalArgumentException("ApacheHttpClientExecutor execute result status line is " + closeableHttpResponse.getStatusLine());
        }
    }
}
