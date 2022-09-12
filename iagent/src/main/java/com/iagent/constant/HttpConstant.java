package com.iagent.constant;

/**
 * <p>Http Executor Constant</p>
 */
public class HttpConstant {
    /**
     * <b>connection http server time (millisecond)</b>
     */
    public static final int CONNECTION_TIME = 2000;

    /**
     * <b>get data from http server read time (millisecond)</b>
     */
    public static final int READ_TIME = 10000;

    /**
     * <b>send http type</b>
     */
    public static final String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_JSON_UTF8 = "application/json;charset=utf-8";
    public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";
    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

    /**
     * 排序最小值
     */
    public static final int MIN_ORDER = 1;

    /**
     * 排序最大值
     */
    public static final int MAX_ORDER = 100;
}
