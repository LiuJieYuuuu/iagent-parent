package com.iagent.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class ResourceUtils {

    public static final char DEFAULT_ALL_CHAR = '*';

    public static final String DEFAULT_CLASS_PATH = "**/*.class";

    public static final String CLASS_FILE_SUFFIX = ".class";

    public static final String URL_PROTOCOL_WAR = "war";

    public static final String FILE_URL_PREFIX = "file:";

    public static final String URL_PROTOCOL_FILE = "file";

    public static final String URL_PROTOCOL_JAR = "jar";

    public static final String JAR_URL_PREFIX = "jar:";

    public static final String JAR_URL_SEPARATOR = "!/";

    public static final String WAR_URL_SEPARATOR = "*/";

    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";

    public static final String URL_PROTOCOL_VFS = "vfs";
    // url分隔符号
    public static final String URL_SEPARATOR = "/";

    public static boolean isFileURL(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_FILE.equals(protocol) || URL_PROTOCOL_VFSFILE.equals(protocol) ||
                URL_PROTOCOL_VFS.equals(protocol));
    }

    public static void useCachesIfNecessary(URLConnection con) {
        con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
    }

    public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
        if (resourceUrl == null) {
            throw new IllegalArgumentException("Resource URL must not be null");
        }
        if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
            throw new FileNotFoundException(
                    description + " cannot be resolved to absolute file path " +
                            "because it does not reside in the file system: " + resourceUrl);
        }
        try {
            //TODO 这么转换感觉可能有问题
            return new File(resourceUrl.toURI().getSchemeSpecificPart());
        }
        catch (URISyntaxException ex) {
            // Fallback for URLs that are not valid URIs (should hardly ever happen).
            return new File(resourceUrl.getFile());
        }
    }
}
