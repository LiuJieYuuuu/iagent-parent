package com.iagent.request.parse;

import com.iagent.annotation.Order;
import com.iagent.constant.HttpConstant;
import com.iagent.json.JSON;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * @author liujieyu
 * @date 2022/8/22 19:09
 * @desciption 八大基本数据类型结果解析器
 */

@Order(HttpConstant.MIN_ORDER)
public class NumberResultParser implements ResultParser {

    private static final Logger logger = LogFactory.getLogger(NumberResultParser.class);

    @Override
    public boolean isParser(Class<?> cls) {
        if (int.class.isAssignableFrom(cls) || float.class.isAssignableFrom(cls)
                || double.class.isAssignableFrom(cls) || long.class.isAssignableFrom(cls)
                || byte.class.isAssignableFrom(cls) || char.class.isAssignableFrom(cls)
                || boolean.class.isAssignableFrom(cls) || short.class.isAssignableFrom(cls)) {
            return true;
        }
        return false;
    }

    @Override
    public <T> T parseResult(Class<T> returnClassType, CloseableHttpResponse closeableHttpResponse) throws Exception {
        if (isOk(closeableHttpResponse)) {
            // 成功
            String resultStr = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
            if (int.class.isAssignableFrom(returnClassType)) {
                Integer i = Integer.parseInt(resultStr);
                return (T) i;
            } else if (float.class.isAssignableFrom(returnClassType)) {
                Float f = Float.parseFloat(resultStr);
                return (T) f;
            } else if (double.class.isAssignableFrom(returnClassType)) {
                Double d = Double.parseDouble(resultStr);
                return (T) d;
            } else if (long.class.isAssignableFrom(returnClassType)) {
                Long l = Long.parseLong(resultStr);
                return (T) l;
            } else if (byte.class.isAssignableFrom(returnClassType)) {
                Byte b = Byte.parseByte(resultStr);
                return (T) b;
            } else if (char.class.isAssignableFrom(returnClassType)) {
                // char类型取第二位， 第一位是 ""
                Character character = resultStr.toCharArray()[1];
                return (T) character;
            } else if (boolean.class.isAssignableFrom(returnClassType)) {
                Boolean b = Boolean.parseBoolean(resultStr);
                return (T) b;
            } else if (short.class.isAssignableFrom(returnClassType)) {
                Short s = Short.parseShort(resultStr);
                return (T) s;
            } else {
                return (T) resultStr;
            }
        } else {
            // 失败
            throw new IllegalArgumentException("ApacheHttpClientExecutor execute result status line is " + closeableHttpResponse.getStatusLine());
        }
    }
}
