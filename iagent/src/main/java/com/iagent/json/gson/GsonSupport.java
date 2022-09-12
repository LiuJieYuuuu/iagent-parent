package com.iagent.json.gson;

import com.iagent.json.JSONSupport;
import com.iagent.util.ClassUtils;

/**
 * gson adapter json framework
 */
public class GsonSupport implements JSONSupport {

    private com.google.gson.Gson gson;

    private final String CLASS_NAME = "com.google.gson.Gson";

    public GsonSupport () throws Exception {
        super();
        Class<?> aClass = ClassUtils.getClassLoader().loadClass(CLASS_NAME);
        if (aClass == null) {
            aClass = Class.forName(CLASS_NAME);
        }
        gson = (com.google.gson.Gson) aClass.newInstance();
    }

    @Override
    public <T> T getJSONObject(String text, Class<T> clazz) {
        return gson.fromJson(text, clazz);
    }

    @Override
    public String toJSONString(Object object) {
        return gson.toJson(object);
    }
}
