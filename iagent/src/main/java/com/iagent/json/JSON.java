package com.iagent.json;

import com.iagent.json.fastjson.FastJsonSupport;
import com.iagent.json.gson.GsonSupport;
import com.iagent.json.jackson.JacksonSupport;

public final class JSON {

    private static JSONSupport support = null;

    static {
        useFastjsonSupport();
        useGsonSupport();
        useJacksonSupport();
    }

    private static void useFastjsonSupport() {
        useJson(FastJsonSupport.class);
    }

    private static void useGsonSupport() {
        useJson(GsonSupport.class);
    }

    private static void useJacksonSupport () {
        useJson(JacksonSupport.class);
    }

    public static void useJson(Class<? extends JSONSupport> clazz) {
        try {
            if (support == null)
                support = clazz.newInstance();
        } catch (Exception e) {
            //adapter json frame work
        }
    }

    public static <T> T getJSONObject (String text, Class<T> clazz) {
        return support.getJSONObject(text,clazz);
    }

    public static String toJSONString(Object object) {
        return support.toJSONString(object);
    }
}
