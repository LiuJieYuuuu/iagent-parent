package com.iagent.scan.kernel;

import com.iagent.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;

/**
 * @author liujieyu
 * @date 2022/4/25 20:15
 * @desciption Class文件路径对象
 */
public class UrlResource {

    /**
     * Original URL
     */
    private URL url;

    /**
     * Original URI
     */
    private URI uri;

    /**
     * original file
     */
    private File file;

    /**
     * 临时将扫描路径储存
     */
    private String path;

    /**
     * Create Constructor base on th given uri
     * @param uri
     * @throws MalformedURLException
     */
    public UrlResource(URI uri) {
        this.uri = uri;
        try {
            this.url = uri.toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.file = new File(this.uri);
    }

    public UrlResource(String url) throws MalformedURLException {
        this.url = new URL(url);
        try {
            this.uri = new URI(java.net.URLEncoder.encode(this.url.toString(), "UTF-8"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.file = new File(this.uri);
    }

    /**
     * Create Constructor base on th given Url
     * @param url
     */
    public UrlResource(URL url) {
        this.url = url;
        try {
            this.uri = this.url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (ResourceUtils.URL_PROTOCOL_FILE.equals(url.getProtocol())) {
            this.file = new File(this.uri);
        }
    }

    /**
     * Create Constructor base on th given File
     * @param file
     */
    public UrlResource(File file) throws MalformedURLException {
        this.file = file;
        this.uri = file.toURI();
        this.url = uri.toURL();
    }

    public URL getUrl() {
        return url;
    }

    public URI getUri() {
        return uri;
    }

    public InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
        ResourceUtils.useCachesIfNecessary(con);
        try {
            return con.getInputStream();
        }
        catch (IOException ex) {
            // Close the HTTP connection (if applicable).
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
            throw ex;
        }
    }

    public String getProtocl() {
        return this.url.getProtocol();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "UrlResource{" +
                "url=" + url +
                ", uri=" + uri +
                ", file=" + file +
                ", path='" + path + '\'' +
                '}';
    }

    public String getDescription() {
        return "URL [" + this.url + "]";
    }

    public File getFile() throws IOException {
        URL url = getUrl();
        return ResourceUtils.getFile(url, getDescription());
    }

    public boolean exists() {
        try {
            URL url = getUrl();
            if (ResourceUtils.isFileURL(url)) {
                // Proceed with file system resolution
                return getFile().exists();
            }
            else {
                // Try a URL connection content-length header
                URLConnection con = url.openConnection();
                customizeConnection(con);
                HttpURLConnection httpCon =
                        (con instanceof HttpURLConnection ? (HttpURLConnection) con : null);
                if (httpCon != null) {
                    int code = httpCon.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        return true;
                    }
                    else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                        return false;
                    }
                }
                if (con.getContentLengthLong() > 0) {
                    return true;
                }
                if (httpCon != null) {
                    // No HTTP OK status, and no content-length header: give up
                    httpCon.disconnect();
                    return false;
                }
                else {
                    // Fall back to stream existence: can we open the stream?
                    getInputStream().close();
                    return true;
                }
            }
        }
        catch (IOException ex) {
            return false;
        }
    }

    /**
     * set url connection
     * @param con
     * @throws IOException
     */
    protected void customizeConnection(URLConnection con) throws IOException {
        ResourceUtils.useCachesIfNecessary(con);
        if (con instanceof HttpURLConnection) {
            customizeConnection((HttpURLConnection) con);
        }
    }

}
