package com.iagent.json.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iagent.exception.JsonException;
import com.iagent.json.JSONSupport;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.util.ClassUtils;

/**
 * jackson adapter json framework
 */
public class JacksonSupport implements JSONSupport {

    private static final Logger logger = LogFactory.getLogger(JacksonSupport.class);

    private ObjectMapper objectMapper;

    private final String CLASS_NAME = "com.fasterxml.jackson.databind.ObjectMapper";

    public JacksonSupport () throws Exception {
        super();
        Class<?> aClass = ClassUtils.getClassLoader().loadClass(CLASS_NAME);
        if (aClass == null) {
            aClass = Class.forName(CLASS_NAME);
        }
        objectMapper = (ObjectMapper) aClass.newInstance();
    }

    @Override
    public <T> T getJSONObject(String text, Class<T> clazz) {
        try {
            return objectMapper.readValue(text, clazz);
        } catch (JsonProcessingException e) {
            logger.error("jackson serializer fail!", e);
            throw new JsonException(e);
        }
    }

    @Override
    public String toJSONString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Jackson Write Value Error!", e);
            return null;
        }
    }
}
