package com.iagent.scan.kernel;

import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.scan.ClassPathBeanScanner;
import com.iagent.scan.kernel.classreader.ClassReader;
import com.iagent.scan.kernel.classreader.SimpleClassReader;
import com.iagent.scan.resolver.JarFileResourcePatternResolver;
import com.iagent.scan.resolver.ResourcePatternResolver;
import com.iagent.util.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author liujieyu
 * @date 2022/4/25 20:11
 * @desciption resource scan
 * @see com.iagent.scan.ClassPathBeanScanner
 */
public abstract class AbstractResourceScanner {

    private static final Logger logger = LogFactory.getLogger(AbstractResourceScanner.class);

    private static final String[] DEFAULT_RESOLVER_CLASS = new String[]{
            "com.iagent.scan.resolver.FileResourcePatternResolver",
            "com.iagent.scan.resolver.JarFileResourcePatternResolver"
    };
    // resolver Url resource
    private List<ResourcePatternResolver> resourcePatternResolvers = new ArrayList<>(4);

    private List<ResourcePatternResolver> getResourcePatternResolvers() {
        return this.resourcePatternResolvers;
    }
    // class reader
    private ClassReader classReader = new SimpleClassReader();

    public AbstractResourceScanner() {
        for (String resolverClass : DEFAULT_RESOLVER_CLASS) {
            ResourcePatternResolver resourcePatternResolver = BeanUtils.instance(resolverClass, ResourcePatternResolver.class);
            if (null != resourcePatternResolver) {
                this.resourcePatternResolvers.add(resourcePatternResolver);
            }
        }
    }

    /**
     * find package all class and under package class
     * @param locationPath like org.example or org.example.**.agent or org.example.**
     * @return
     */
    protected Set<Class> findAllClassByClassPath(String locationPath) {
        Assert.notNull(locationPath, " Scan Path is NULL");
        Set<Class> classSet = new LinkedHashSet<>(16);
        Set<UrlResource> urlResources = null;
        String pattern = null;
        try {
            locationPath = StringUtils.deleteSpecifiedChar(StringUtils.replace(locationPath, ".", "/"),
                    ResourceUtils.DEFAULT_ALL_CHAR, false);
            if (!locationPath.endsWith("/")) {
                locationPath += "/";
            }
            locationPath += ResourceUtils.DEFAULT_CLASS_PATH;
            pattern = getPatternByLocationPath(locationPath);
            urlResources = doFindResourceUrl(locationPath);
        } catch (IOException e) {
            logger.error("Not Found Location Path [" + locationPath + "] because:" + e.getMessage(), e);
        }

        for (UrlResource urlResource : urlResources) {
            for (ResourcePatternResolver resourcePatternResolver : getResourcePatternResolvers()) {
                if (resourcePatternResolver.isResourceType(urlResource)) {
                    Set<UrlResource> resources = resourcePatternResolver.getResources(urlResource, pattern);
                    if (CollectionUtils.isNotEmpty(resources)) {
                        for (UrlResource urlResource1 : resources) {
                            Class<?> aClass = classReader.getClassInfoByUrlResource(urlResource1);
                            if (aClass != null) {
                                classSet.add(aClass);
                            }
                        }
                    }
                }
            }
        }

        return classSet;
    }

    /**
     * get all file
     * @param patternLocation location path array
     * @return
     */
    private Set<UrlResource> doFindResourceUrl(String patternLocation) throws IOException {
        Set<UrlResource> urlResources = new LinkedHashSet<>(16);
        ClassLoader cl = ClassUtils.getClassLoader();
        String path = reserveRootPath(patternLocation);
        // load all class
        if ("".equals(path)) {
            loadAllClassRootDir(cl, urlResources);
        } else {
            //load specified package
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            Enumeration<URL> resources = cl == null ? ClassLoader.getSystemResources(path) : cl.getResources(path);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                UrlResource urlResource = new UrlResource(url);
                urlResource.setPath(path);
                urlResources.add(urlResource);
                if (logger.isDebugEnabled()) {
                    logger.debug("URL [" + url + "]");
                }
            }
        }

        return urlResources;
    }

    /**
     * get root path
     * @param pattern remove scan Class path *, reserve root path
     * @return
     */
    private String reserveRootPath(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            return StringUtils.EMPTY_STRING;
        }
        int end = pattern.indexOf("*");
        int lastEnd = pattern.substring(0, end).lastIndexOf("/");
        return pattern.substring(0, lastEnd + 1);
    }

    private String getPatternByLocationPath(String patternPath) {
        String path = reserveRootPath(patternPath);
        patternPath = patternPath.substring(patternPath.indexOf(path) + path.length());
        if (patternPath.startsWith("/")) {
            return patternPath.substring(1);
        }
        return patternPath;
    }

    private void loadAllClassRootDir(ClassLoader classLoader, Set<UrlResource> urlResources) {
        if (classLoader instanceof URLClassLoader) {
            try {
                for (URL url : ((URLClassLoader) classLoader).getURLs()) {
                    try {
                        UrlResource jarResource = (ResourceUtils.URL_PROTOCOL_JAR.equals(url.getProtocol()) ?
                                new UrlResource(url) :
                                new UrlResource(ResourceUtils.JAR_URL_PREFIX + url + ResourceUtils.JAR_URL_SEPARATOR));
                        if (jarResource.exists()) {
                            urlResources.add(jarResource);
                        }
                    }
                    catch (MalformedURLException ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Cannot search for matching files underneath [" + url +
                                    "] because it cannot be converted to a valid 'jar:' URL: " + ex.getMessage());
                        }
                    }
                }
            }
            catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Cannot introspect jar files ClassLoader [" + classLoader +
                            "] error: " + ex);
                }
            }
        }

        if (classLoader == ClassLoader.getSystemClassLoader()) {
            addClassPathManifestEntries(urlResources);
        }

        if (classLoader != null) {
            try {
                // parent classloader
                loadAllClassRootDir(classLoader.getParent(), urlResources);
            }
            catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Cannot introspect jar files in parent ClassLoader since [" + classLoader +
                            "] does not support 'getParent()': " + ex);
                }
            }
        }
    }

    private void addClassPathManifestEntries(Set<UrlResource> result) {
        try {
            String javaClassPathProperty = System.getProperty("java.class.path");
            String separator = System.getProperty("path.separator");
            for (String path : javaClassPathProperty.split(separator)) {
                try {
                    String filePath = new File(path).getAbsolutePath();
                    UrlResource jarResource = new UrlResource(ResourceUtils.JAR_URL_PREFIX +
                            ResourceUtils.FILE_URL_PREFIX + filePath + ResourceUtils.JAR_URL_SEPARATOR);
                    // Potentially overlapping with URLClassLoader.getURLs() result above!
                    if (!result.contains(jarResource) && !hasDuplicate(filePath, result) && jarResource.exists()) {
                        result.add(jarResource);
                    }
                }
                catch (MalformedURLException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Cannot search for matching files underneath [" + path +
                                "] because it cannot be converted to a valid 'jar:' URL: " + ex.getMessage());
                    }
                }
            }
        }
        catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to evaluate 'java.class.path' manifest entries: " + ex);
            }
        }
    }

    private boolean hasDuplicate(String filePath, Set<UrlResource> result) {
        if (result.isEmpty()) {
            return false;
        }
        String duplicatePath = (filePath.startsWith("/") ? filePath.substring(1) : "/" + filePath);
        try {
            return result.contains(new UrlResource(ResourceUtils.JAR_URL_PREFIX + ResourceUtils.FILE_URL_PREFIX +
                    duplicatePath + ResourceUtils.JAR_URL_SEPARATOR));
        } catch (MalformedURLException ex) {
            // Ignore: just for testing against duplicate.
            return false;
        }
    }

}
