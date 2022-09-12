package com.iagent.json;


public interface JSONSupport  {

    //JSON对象方法
    <T> T getJSONObject(String text, Class<T> clazz);

    /**
     * 将对象JSON序列化成String对象
     * @param object
     * @return
     */
    String toJSONString(Object object);
}
