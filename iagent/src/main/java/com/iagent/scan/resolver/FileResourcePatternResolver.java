package com.iagent.scan.resolver;

import com.iagent.logging.LogFactory;
import com.iagent.logging.Logger;
import com.iagent.scan.kernel.UrlResource;
import com.iagent.util.Assert;
import com.iagent.util.ResourceUtils;
import com.iagent.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author liujieyu
 * @date 2022/5/6 17:43
 * @desciption deal with file protocol
 */
public class FileResourcePatternResolver implements ResourcePatternResolver {

    private static final Logger logger = LogFactory.getLogger(FileResourcePatternResolver.class);

    @Override
    public boolean isResourceType(UrlResource urlResource) {
        String protocol = urlResource.getUrl().getProtocol();
        return ResourceUtils.URL_PROTOCOL_FILE.equals(protocol);
    }

    @Override
    public Set<UrlResource> getResources(UrlResource rootDirResource, String pattern) {
        Assert.notNull(pattern, " Pattern Path is Null");
        File rootDir = null;
        try {
            rootDir = rootDirResource.getFile().getAbsoluteFile();
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot search for matching files underneath [" + rootDirResource +
                        "] in the file system: " + e.getMessage());
            }
        }
        String scanPath = rootDirResource.getPath();
        return doFindPatternFileSystemResources(rootDir,
                StringUtils.replace(rootDir.getPath(), "\\", "/") + "/" + pattern, scanPath);
    }

    /**
     * get all resources for directory of rootDir by pattern
     * @param rootDir
     * @param pattern
     * @return
     */
    protected Set<UrlResource> doFindPatternFileSystemResources(File rootDir, String pattern, String scanPath) {
        if (rootDir == null || !rootDir.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping [" + rootDir.getAbsolutePath() + "] because it does not exist");
            }
            return Collections.EMPTY_SET;
        }
        if (!rootDir.isDirectory()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping [" + rootDir.getAbsolutePath() + "] because it does not a directory");
            }
            return Collections.EMPTY_SET;
        }
        if (!rootDir.canRead()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping [" + rootDir.getAbsolutePath() + "] because the application is not allowed to read the directory");
            }
            return Collections.EMPTY_SET;
        }
        if (rootDir.listFiles() == null || rootDir.listFiles().length == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping [" + rootDir.getAbsolutePath() + "] because the directory is null or the directory is empty");
            }
            return Collections.EMPTY_SET;
        }
        Set<UrlResource> urlResources = new LinkedHashSet<>(8);
        File[] files = rootDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                Set<UrlResource> childUrlResources = doFindPatternFileSystemResources(file, pattern, scanPath);
                urlResources.addAll(childUrlResources);
            } else {
                // is file
                try {
                    String subPattern = getPatternByLocationPath(pattern);
                    // get relative path
                    String relativePath = StringUtils.replace(file.getAbsolutePath(), "\\", "/");
                    String reserveRootPath = reserveRootPath(pattern);
                    relativePath = relativePath.substring(relativePath.indexOf(reserveRootPath) + reserveRootPath.length());
                    if (getMatcher(subPattern, relativePath)) {
                        UrlResource urlResource = new UrlResource(file);
                        urlResource.setPath(scanPath);
                        urlResources.add(urlResource);
                    }
                } catch (MalformedURLException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Cannot search for matching file [" + file + "] because it cannot be converted to a valid 'file:' URL: " + e.getMessage());
                    }
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
        return pattern.substring(0, lastEnd);
    }

    private String getPatternByLocationPath(String patternPath) {
        String path = reserveRootPath(patternPath);
        patternPath = patternPath.substring(patternPath.indexOf(path) + path.length());
        if (patternPath.startsWith("/")) {
            return patternPath.substring(1);
        }
        return patternPath;
    }
}
