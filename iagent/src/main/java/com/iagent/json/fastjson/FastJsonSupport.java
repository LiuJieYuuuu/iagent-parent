package com.iagent.json.fastjson;

import com.alibaba.fastjson.JSON;
import com.iagent.exception.JsonException;
import com.iagent.json.JSONSupport;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.util.ClassUtils;

/*
 * fastjson support
 */
public class FastJsonSupport implements JSONSupport {

    private Logger logger = LogFactory.getLogger(FastJsonSupport.class);

    private final String CLASS_NAME = "com.alibaba.fastjson.JSON";

    public FastJsonSupport() {
        super();
        try {
            Class<?> aClass = ClassUtils.getClassLoader().loadClass(CLASS_NAME);
            if (aClass == null) {
                aClass = Class.forName(CLASS_NAME);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("use fast json");
            }
        } catch (Throwable t) {
            throw new JsonException("not alibaba fastjson framework in project");
        }
    }

    @Override
    public <T> T getJSONObject(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    @Override
    public String toJSONString(Object object) {
        return com.alibaba.fastjson.JSON.toJSONString(object);
    }

}
