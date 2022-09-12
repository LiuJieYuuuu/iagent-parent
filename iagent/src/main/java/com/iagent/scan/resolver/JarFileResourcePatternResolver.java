package com.iagent.scan.resolver;

import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.scan.kernel.UrlResource;
import com.iagent.util.Assert;
import com.iagent.util.ResourceUtils;
import com.iagent.util.StringUtils;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

/**
 * @author liujieyu
 * @date 2022/5/6 17:43
 * @desciption deal with Jar file protocol
 */
public class JarFileResourcePatternResolver implements ResourcePatternResolver {

    private static final Logger logger = LogFactory.getLogger(JarFileResourcePatternResolver.class);

    @Override
    public boolean isResourceType(UrlResource urlResource) {
        String protocol = urlResource.getUrl().getProtocol();
        return (ResourceUtils.URL_PROTOCOL_JAR.equals(protocol));
    }

    @Override
    public Set<UrlResource> getResources(UrlResource rootDirResource, String pattern) {
        Assert.notNull(pattern, " Pattern Path is Null");
        URLConnection con = null;
        try {
            con = rootDirResource.getUrl().openConnection();
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping [" + rootDirResource.getUrl() + "] because it's not open connection");
            }
            return Collections.EMPTY_SET;
        }
        JarFile jarFile = null;
        String jarFileUrl = null;
        String rootEntryPath = null;
        boolean closeJarFile = false;

        if (con instanceof JarURLConnection) {
            // Should usually be the case for traditional JAR files.
            JarURLConnection jarCon = (JarURLConnection) con;
            ResourceUtils.useCachesIfNecessary(jarCon);
            try {
                jarFile = jarCon.getJarFile();
                jarFileUrl = jarCon.getJarFileURL().toExternalForm();
                JarEntry jarEntry = jarCon.getJarEntry();
                rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
                closeJarFile = !jarCon.getUseCaches();
            } catch (IOException e) {
                logger.error("Skipping [" + con + "] because it's not open jar", e);
                return Collections.EMPTY_SET;
            }
        }
        else {
            // No JarURLConnection need to resort to URL file parsing.
            // We'll also handle paths with and without leading "file:" prefix.
            String urlFile = rootDirResource.getUrl().getFile();
            try {
                int separatorIndex = urlFile.indexOf(ResourceUtils.WAR_URL_SEPARATOR);
                if (separatorIndex == -1) {
                    separatorIndex = urlFile.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
                }
                if (separatorIndex != -1) {
                    jarFileUrl = urlFile.substring(0, separatorIndex);
                    rootEntryPath = urlFile.substring(separatorIndex + 2);  // both separators are 2 chars
                    jarFile = getJarFile(jarFileUrl);
                }
                else {
                    jarFile = new JarFile(urlFile);
                    jarFileUrl = urlFile;
                    rootEntryPath = "";
                }
                closeJarFile = true;
            } catch (ZipException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipping invalid jar classpath entry [" + urlFile + "]");
                }
                return Collections.EMPTY_SET;
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipping invalid jar classpath entry [" + urlFile + "]");
                }
                return Collections.EMPTY_SET;
            }
        }

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Looking for matching resources in jar file [" + jarFileUrl + "]");
            }
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash to allow for proper matching.
                // The Sun JRE does not return a slash here, but BEA JRockit does.
                rootEntryPath = rootEntryPath + "/";
            }
            Set<UrlResource> result = new LinkedHashSet<>(8);
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    // .class file is add result
                    if (relativePath.endsWith(".class") && getMatcher(pattern, relativePath)) {
                        result.add(new UrlResource(new URL(rootDirResource.getUrl(), relativePath)));
                    }
                }
            }
            return result;
        } catch (MalformedURLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping [" + rootDirResource.getUrl() + "] because it's loading jar error:" + e.getMessage());
            }
            return Collections.EMPTY_SET;
        } finally {
            if (closeJarFile) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Skipping [" + jarFile + "] because it's not close");
                    }
                }
            }
        }
    }

    /**
     * Resolve the given jar file URL into a JarFile object.
     */
    private JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
            try {
                return new JarFile(new URI(StringUtils.replace(jarFileUrl, " ", "%20")).getSchemeSpecificPart());
            }
            catch (URISyntaxException ex) {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                return new JarFile(jarFileUrl.substring(ResourceUtils.FILE_URL_PREFIX.length()));
            }
        }
        else {
            return new JarFile(jarFileUrl);
        }
    }
}
