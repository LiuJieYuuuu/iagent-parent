package com.iagent.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * File and Stream Utils
 */
public class IOUtils {

    /**
     * 将字节数组转换成文件
     * @param path
     * @param bytes
     */
    public static void writeToFileByBytes(String path, byte[] bytes) {
        File file = new File(path);
        OutputStream outputStream = null;
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();

            }
            outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
