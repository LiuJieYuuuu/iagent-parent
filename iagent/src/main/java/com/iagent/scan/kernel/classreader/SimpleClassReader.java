package com.iagent.scan.kernel.classreader;

import com.iagent.annotation.IagentUrl;
import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.scan.kernel.UrlResource;
import com.iagent.util.ClassUtils;
import com.iagent.util.ResourceUtils;
import com.iagent.util.StringUtils;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarEntry;

/**
 * an simple class reader
 */
public class SimpleClassReader implements ClassReader {

    private static final Logger logger = LogFactory.getLogger(SimpleClassReader.class);

    @Override
    public Class<?> getClassInfoByUrlResource(UrlResource urlResource) {
        if (ResourceUtils.URL_PROTOCOL_FILE.equals(urlResource.getProtocl())) {
            String path = ClassUtils.getClassLoader().getClass().getResource("/").getPath();
            try {
                String absolutePath = urlResource.getFile().getAbsolutePath();
                absolutePath = StringUtils.replace(absolutePath, "\\", "/");
                if (!absolutePath.startsWith("/")) {
                    absolutePath = "/" + absolutePath;
                }
                // get com/xxx/xxx/xxx.class
                if (absolutePath.indexOf(path) == -1) {
                    absolutePath = absolutePath.substring(absolutePath.indexOf(urlResource.getPath()), absolutePath.indexOf(ResourceUtils.CLASS_FILE_SUFFIX));
                } else {
                    absolutePath = absolutePath.substring(absolutePath.indexOf(path) + path.length(),
                            absolutePath.indexOf(ResourceUtils.CLASS_FILE_SUFFIX));
                }
                String className = StringUtils.replace(absolutePath, "/", ".");
                return ClassUtils.getInterfaceClass(className, IagentUrl.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (ResourceUtils.URL_PROTOCOL_JAR.equals(urlResource.getProtocl())) {
            URL url = urlResource.getUrl();
            try {
                JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                JarEntry jarEntry = urlConnection.getJarEntry();
                String classPath = jarEntry.getName();
                classPath = classPath.substring(0, classPath.indexOf(ResourceUtils.CLASS_FILE_SUFFIX));
                String className = StringUtils.replace(classPath, "/", ".");
                return ClassUtils.getInterfaceClass(className, IagentUrl.class);
            } catch (IOException e) {
                logger.error("the url [" + url + "] is not reader", e);
            }
        }
        return null;
    }

}
