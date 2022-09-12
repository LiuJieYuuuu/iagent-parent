package com.iagent.request.parse;

import com.iagent.annotation.Order;
import com.iagent.constant.HttpConstant;
import com.iagent.util.IOUtils;
import com.iagent.util.RandomUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.File;

/**
 * 文件类型结果返回解析
 */

@Order(HttpConstant.MAX_ORDER / 2)
public class BinaryResultParser implements ResultParser {

    @Override
    public boolean isParser(Class<?> cls) {
        return (File.class.isAssignableFrom(cls) || byte[].class.isAssignableFrom(cls));
    }

    @Override
    public <T> T parseResult(Class<T> returnClassType, CloseableHttpResponse closeableHttpResponse) throws Exception {
        if (isOk(closeableHttpResponse)) {
            // 成功
            if (File.class.isAssignableFrom(returnClassType)) {
                byte[] bytes = EntityUtils.toByteArray(closeableHttpResponse.getEntity());
                String filePath = System.getProperty("java.io.tmpdir") + File.separator + RandomUtils.getId();
                IOUtils.writeToFileByBytes(filePath, bytes);
                return (T) new File(filePath);
            } else if (byte[].class.isAssignableFrom(returnClassType)){
                byte[] bytes = EntityUtils.toByteArray(closeableHttpResponse.getEntity());
                return (T) bytes;
            } else{
                return null;
            }
        } else {
            // 失败
            throw new IllegalArgumentException("ApacheHttpClientExecutor execute result status line is " + closeableHttpResponse.getStatusLine());
        }
    }

}
