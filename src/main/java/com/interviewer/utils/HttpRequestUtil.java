package com.interviewer.utils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpClient工具类
 */
public class HttpRequestUtil {

//    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);


    private RequestConfig requestConfig;

    private String encoding = "UTF-8";
    private CloseableHttpClient httpClient;


    public HttpRequestUtil() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(250);
        cm.setMaxTotal(500);

        httpClient = HttpClients.custom().setConnectionManager(cm).build();
        Integer socketTimeout = 60 * 1000;
        Integer connectTimeout = 60 * 1000;
        Integer connectionRequestTimeout = 60 * 1000;
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();
    }

    /**
     * @param url      请求url
     * @param postData POST请求时form表单封装的数据 没有时传null
     * @return response返回的文本数据
     * @throws Exception
     */
    public String doPostRequest(String url, Map<String, Object> postData) throws Exception {

        return doPostRequest(url, postData, null);
    }

    /**
     * @param url      请求url
     * @param postData POST文件上传请求时form表单封装的数据 没有时传null
     * @param header   头信息,没有时传null
     * @return response返回的文本数据
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public String doFileUploadPostRequest(String url, Map<String, Object> postData, Map<String, Object> header) throws Exception {
        CloseableHttpResponse response = null;
        String result = "";
        try {
            if (null == postData) {
                postData = new HashMap<String, Object>();
            }

            // 如果postData为null
            if (postData == null || postData.size() <= 0) return result;
            //post参数传递
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            for (Map.Entry<String, Object> entry : postData.entrySet()) {
                if (null != entry.getValue() && !"".equals(entry.getValue())) {
                    if ("fileName".equals(entry.getKey())) {
                        File file = (File) entry.getValue();
                        builder.addBinaryBody(entry.getKey().toString().trim(), file,
                                ContentType.DEFAULT_BINARY, file.getName());
                    } else {
                        builder.addTextBody(entry.getKey().toString().trim(), entry.getValue().toString(),
                                ContentType.DEFAULT_BINARY);
                    }
                }
            }
            // 目标地址
            HttpPost httpPost = new HttpPost(url);

            //头信息
            if (null != header) {
                for (Map.Entry<String, Object> entry : header.entrySet()) {
                    if (null != entry.getValue()) {
                        httpPost.setHeader(entry.getKey(), entry.getValue().toString());
                    }
                }
            }
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(builder.build()); // 设置参数给Post
            // 得到返回的response.
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = getResult(entity);
        } catch (Exception e) {
            e.printStackTrace();
            if (null != httpClient) {
                httpClient.close();
            }
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity()); //会自动释放连接
                response.close();
            }
        }
        return result;
    }

    /**
     * @param url      请求url
     * @param postData POST请求时form表单封装的数据 没有时传null
     * @param header   头信息,没有时传null
     * @return response返回的文本数据
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public String doPostRequest(String url, Map<String, Object> postData, Map<String, Object> header) throws Exception {
        CloseableHttpResponse response = null;
        String result = "";
        try {
            if (null == postData) {
                postData = new HashMap<String, Object>();
            }
            //post参数传递
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            for (Map.Entry<String, Object> entry : postData.entrySet()) {
                if (null != entry.getValue()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));

                }
            }
            // 目标地址
            HttpPost httpPost = new HttpPost(url);

            //头信息
            if (null != header) {
                for (Map.Entry<String, Object> entry : header.entrySet()) {
                    if (null != entry.getValue()) {
                        httpPost.setHeader(entry.getKey(), entry.getValue().toString());
                    }
                }
            }
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new UrlEncodedFormEntity(params, encoding)); // 设置参数给Post
            // 得到返回的response.
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = getResult(entity);
        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
            if (null != httpClient) {
                httpClient.close();
            }
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity()); //会自动释放连接
                response.close();
            }
        }
        return result;
    }

    /**
     * @param url 请求url
     * @return response返回的文本数据
     * @throws Exception
     */
    public String doGetRequest(String url) throws Exception {
        return doGetRequest(url, null);
    }

    /**
     * @param url    请求url
     * @param header 头信息,没有时传null
     * @return response返回的文本数据
     * @throws Exception
     */
    public String doGetRequest(String url, Map<String, Object> header) throws Exception {

        CloseableHttpResponse response = null;
        String result = "";
        try {
            HttpGet httpGet = new HttpGet(url);
            //头信息
            if (null != header) {
                for (Map.Entry<String, Object> entry : header.entrySet()) {
                    if (null != entry.getValue()) {
                        httpGet.setHeader(entry.getKey(), entry.getValue().toString());
                    }
                }
            }
            httpGet.setConfig(requestConfig);
            // 得到返回的response.
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = getResult(entity);
        } catch (Exception e) {
            if (null != httpClient) {
                httpClient.close();
            }
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity()); //会自动释放连接
                response.close();
            }
        }
        return result;
    }

    /**
     * @param url    请求url
     * @param header 头信息,没有时传null
     * @return response返回的文本数据
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public String doPostRequest(String url, String xmlStr, Map<String, Object> header) throws Exception {
        CloseableHttpResponse response = null;
        String result = "";
        try {

            // 目标地址
            HttpPost httpPost = new HttpPost(url);

            //头信息
            if (null != header) {
                for (Map.Entry<String, Object> entry : header.entrySet()) {
                    if (null != entry.getValue()) {
                        httpPost.setHeader(entry.getKey(), entry.getValue().toString());
                    }
                }
            }
            StringEntity myEntity = new StringEntity(xmlStr, encoding);
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(myEntity); // 设置参数给Post
            if (null != header && !header.containsKey("content-type")) {
                httpPost.addHeader("content-type", "text/xml;charset=utf-8");
            }
            // 得到返回的response.
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = getResult(entity);
        } catch (Exception e) {
            if (null != httpClient) {
                httpClient.close();
            }
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity()); //会自动释放连接
                response.close();
            }
        }
        return result;
    }

    /**
     * @param url 请求url
     * @return response返回的文本数据
     * @throws Exception
     */
    public HttpEntity doGetEntity(String url) throws Exception {
        return doGetEntity(url, null);
    }


    /**
     * @param url    请求url
     * @param header 头信息,没有时传null
     * @return response返回的文本数据
     * @throws Exception
     */
    public HttpEntity doGetEntity(String url, Map header) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        // 得到返回的response.
        HttpResponse response = httpClient.execute(httpGet);
        return response.getEntity();
    }

    /**
     * 返回结果转String
     *
     * @param entity HttpEntity
     * @return String
     */
    private String getResult(HttpEntity entity) {
        StringBuilder sbResult = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    entity.getContent(), encoding));
            String line;
            while ((line = reader.readLine()) != null) {
                sbResult.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sbResult.toString();
    }

    /**
     * 获取远程的图片到指定路劲
     *
     * @param url          请求url
     * @param fullFileName 文件全路径名称
     * @return bo
     */
   /* public boolean downloadRemoteImg(String url, String fullFileName) {
        boolean bo = false;
        InputStream is = null;
        FileOutputStream output = null;
        try {
            is = doGetEntity(url).getContent();
            File file = new File(fullFileName);
            output = FileUtils.openOutputStream(file);
            IOUtils.copy(is, output);
            bo = true;
        } catch (Exception e) {
            bo = false;
        } finally {
            if (null != output) {
                IOUtils.closeQuietly(output);
            }
            if (null != is) {
                IOUtils.closeQuietly(is);
            }
        }
        return bo;
    }*/


}
