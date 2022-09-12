package com.iagent.scan.resolver;

import com.iagent.scan.kernel.UrlResource;
import com.iagent.util.Assert;
import com.iagent.util.ResourceUtils;
import com.iagent.util.StringUtils;

import java.util.Set;

/**
 * @author liujieyu
 * @date 2022/5/6 16:07
 * @desciption resolver resource
 */
public interface ResourcePatternResolver {

    /**
     * judge resource type
     * @param urlResource
     * @return
     */
    boolean isResourceType(UrlResource urlResource);

    /**
     * get all class of root resource, used regex pattern
     * @param rootDirResource
     * @param pattern
     * @return
     */
    Set<UrlResource> getResources(UrlResource rootDirResource, String pattern);

    /**
     * Match the given subPattern against the given relativePath,
     * according to this PathMatcher's matching strategy.
     * @param subPattern
     * @param relativePath
     * @return
     */
    default boolean getMatcher(String subPattern, String relativePath) {
        Assert.notNull(subPattern, " SubPattern String is Null!");
        Assert.notNull(relativePath, " Relative Path String is Null!");

        if (ResourceUtils.DEFAULT_CLASS_PATH.equals(subPattern)) {
            // **/*.class
            return true;
        }
        if (!subPattern.startsWith("*")) {
            throw new IllegalArgumentException("subPattern Path is Error...");
        }
        if (relativePath.endsWith("/")) {
            relativePath = relativePath.substring(0, relativePath.length() - 1);
        }

        // **/agent/**/*.class  or */agent/**/*.class
        subPattern = subPattern.substring(0, subPattern.indexOf(ResourceUtils.DEFAULT_CLASS_PATH));
        if (subPattern.endsWith("/")) {
            subPattern = subPattern.substring(0, subPattern.length() - 1);
        }
        String[] allLocationPath = subPattern.split("/");
        //get number of no * or '**'
        int pathNum = 0;
        for (String locationPath : allLocationPath) {
            if (!locationPath.contains("*")) {
                pathNum ++;
            }
        }
        // get path index and value
        int index = 0;
        int[] pathIndex = new int[pathNum];
        String[] pathDirs = new String[pathNum];
        for (int i = 0; i < allLocationPath.length ; i++) {
            if (!allLocationPath[i].contains("*")) {
                pathIndex[index] = i;
                pathDirs[index] = allLocationPath[i];
                index ++;
            }
        }
        // check relative path is pass
        int checkId = 0;
        for (int i = 0; i < allLocationPath.length ; i++) {
            if (relativePath.startsWith("/")) {
                relativePath = relativePath.substring(1);
            }
            if (allLocationPath[i].contains("**")) {
                if (checkId == pathDirs.length) {
                    return true;
                } else {
                    String[] relativeLocationPath = relativePath.split("/");
                    int i1 = -1;
                    for (int j = 0; j < relativeLocationPath.length; j++) {
                        if (relativeLocationPath[j].equals(pathDirs[checkId])) {
                            i1 = relativePath.indexOf(pathDirs[checkId]) + pathDirs[checkId].length();
                            break;
                        }
                    }
                    if (i1 < 0 || relativePath.startsWith(pathDirs[checkId])) {
                        return false;
                    }
                    relativePath = relativePath.substring(i1);
                    i = pathIndex[checkId];
                    checkId ++;
                }
            } else if (allLocationPath[i].contains("*")) {
                // remove the first path
                if (checkId == pathDirs.length) {
                    return true;
                } else {
                    if (relativePath.indexOf("/") != -1) {
                        relativePath = relativePath.substring(relativePath.indexOf("/") + 1);
                    } else if (StringUtils.isNotEmpty(relativePath)){
                        return true;
                    }
                }
            } else {
                // check path
                String[] relativeLocationPath = relativePath.split("/");
                for (int j = 0; j < relativeLocationPath.length; j++) {
                    if (relativeLocationPath[j].equals(pathDirs[checkId])) {
                        checkId ++;
                        break;
                    }
                }
            }

        }
        // check is not pass
        if (checkId != pathDirs.length) {
            return false;
        }
        return true;
    }

}
