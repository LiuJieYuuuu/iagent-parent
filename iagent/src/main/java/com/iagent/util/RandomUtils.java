package com.iagent.util;

import java.util.UUID;

/**
 * 生成唯一ID
 */
public class RandomUtils {

    /**
     * 获取唯一ID
     * @return
     */
    public static String getId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
