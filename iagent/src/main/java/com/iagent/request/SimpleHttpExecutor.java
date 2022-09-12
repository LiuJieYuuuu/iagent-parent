package com.iagent.request;

import com.alibaba.fastjson.JSON;
import com.iagent.bean.IagentBean;
import com.iagent.bean.IagentBeanWrapper;
import com.iagent.bean.IagentParamBean;
import com.iagent.config.IagentConfiguration;
import com.iagent.constant.HttpConstant;
import com.iagent.constant.HttpEnum;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * <b> the Simple Common Http Request Executor by http connection</b>
 * the SimpleHttpExecutor is remembering the first Http Executor, so not delete,but it's don't using
 */
@Deprecated
public class SimpleHttpExecutor extends AbstractHttpExecutor {

    public SimpleHttpExecutor(IagentConfiguration configuration) {
        super(configuration);
    }

    /**
     * <b>send HTTP Request Url</b>
     * @param url
     * @param param
     * @param type
     * @param contentType
     * @param connectionTime
     * @param readTime
     * @return
     */
    public String sendHttp(String url, Map param, HttpEnum type,
                           String contentType,int connectionTime,int readTime){
        StringBuilder result = new StringBuilder();
        try{
            String params = null;
            if(param != null && !param.isEmpty()){
                params = changeParam(param);
                if(type.equals(HttpEnum.GET))
                    url += "?" + params;
            }
            URL uri = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
            if(type.equals(HttpEnum.GET)){
                urlConnection.setRequestMethod(type.getRequestMethodType());
                setHttpUrlConnectionProperty(urlConnection,contentType,connectionTime,readTime);
            }else if (type.equals(HttpEnum.POST) || type.equals(HttpEnum.PUT)){
                urlConnection.setRequestMethod(type.getRequestMethodType());
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                setHttpUrlConnectionProperty(urlConnection,contentType,connectionTime,readTime);
                if (params != null){
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                    if(contentType.equals(HttpConstant.APPLICATION_JSON) || contentType.equals(HttpConstant.APPLICATION_JSON_UTF8))
                        writer.write(JSON.toJSONString(param));
                    else
                        writer.write(params);
                    writer.close();
                }
            }

            int responseCode = urlConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String temp = null;
                while ((temp = br.readLine()) != null){
                    result.append(temp);
                }
            }

        }catch (Throwable e){
            e.printStackTrace();
        }

        return result.toString();
    }

    /**
     * <p>filled with params to HttpConnection</p>
     * @param urlConnection
     * @param contentType
     * @param connectionTime
     * @param readTime
     * @throws IOException
     */
    private void setHttpUrlConnectionProperty(HttpURLConnection urlConnection,String contentType,
                                              int connectionTime,int readTime) throws IOException {

        if(contentType != null && !"".equals(contentType)){
            urlConnection.setRequestProperty("Content-Type",contentType);
        }else{
            urlConnection.setRequestProperty("Content-Type",HttpConstant.X_WWW_FORM_URLENCODED);

        }

        urlConnection.setConnectTimeout(connectionTime);
        urlConnection.setReadTimeout(readTime);

        urlConnection.connect();
    }

    /**
     * <b>change params of Map to get Http Request,
     * like key=value&key2=value2</b>
     * @param param
     * @return
     */
    private String changeParam(Map<?,?> param) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : param.entrySet()) {
            sb.append(entry.getKey() == null ? "" : entry.getKey().toString());
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue() == null ? "" : entry.getValue().toString(),"UTF-8"));
            sb.append("&");
        }
        if(sb.length() != 0){
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public Object sendHttp(IagentBeanWrapper bean, Object[] args) {
        IagentBean iagentBean = bean.getBean();
        IagentParamBean paramBean = bean.getParamBean();
        Map<String, Integer> paramIndex = paramBean.getParamIndex();
        Map params = new HashMap();
        for (String param : paramIndex.keySet()) {
            params.put(param, args[paramIndex.get(param)]);
        }
        String s = sendHttp(iagentBean.getUrl(), params, iagentBean.getRequestType(),
                iagentBean.getContentType(), iagentBean.getConnectionTime(), iagentBean.getReadTime());

        return s;
    }
}
