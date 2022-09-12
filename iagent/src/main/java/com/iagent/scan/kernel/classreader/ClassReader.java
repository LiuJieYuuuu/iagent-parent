package com.iagent.scan.kernel.classreader;

import com.iagent.scan.kernel.UrlResource;

/**
 * read class file by UrlResource
 * @see com.iagent.scan.kernel.UrlResource
 */
public interface ClassReader {

    Class<?> getClassInfoByUrlResource(UrlResource urlResource);

}
