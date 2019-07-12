package com.github.rxyor.common.util.httclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.rxyor.common.core.exception.CarpIOException;
import com.github.rxyor.common.core.model.R;
import com.github.rxyor.common.core.util.RUtil;
import com.github.rxyor.common.util.httclient.config.HttpConnConfig;
import com.github.rxyor.common.util.io.IOUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-12 Fri 11:10:00
 * @since 1.0.0
 */
public class Request {

    public static final int SUCCESS = 200;

    public enum Method {
        GET,
        POST,
        PUT,
        DELETE,
        HEAD,
        PATCH,
        TRACE,
        OPTIONS
    }

    /**
     * 请求URL
     */
    private String url;

    /**
     * JSON响应体类型
     */
    private TypeReference dataType;

    /**
     *HTTP连接配置
     */
    private HttpConnConfig httpConnConfig;

    /**
     *请求体
     */
    private String json;

    /**
     *请求参数
     */
    private final List<NameValuePair> params = new ArrayList<>(16);

    /**
     *请求头
     */
    private final List<Header> headers = new ArrayList<>(16);

    private Request(String url) {
        this.url = url;
    }

    public R get() {
        return execute(Method.GET);
    }

    public R post() {
        return execute(Method.POST);
    }

    public R put() {
        return execute(Method.PUT);
    }

    public R delete() {
        return execute(Method.DELETE);
    }

    public R head(){
        return execute(Method.HEAD);
    }

    public R patch(){
        return execute(Method.PATCH);
    }

    public R trace(){
        return execute(Method.TRACE);
    }

    public R options(){
        return execute(Method.OPTIONS);
    }

    /**
     * 设置URL
     *
     * @param url
     * @return
     */
    public static Request url(String url) {
        return new Request(url);
    }

    /**
     * http basic请求
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public Request httpBasic(String username, String password) {
        if (StringUtils.isBlank(username)) {
            return this;
        }
        String pwd = password == null ? "" : password;
        String crypt = username + ":" + pwd;
        String value = "Basic " + Base64.getEncoder().encodeToString(crypt.getBytes(Charset.forName("UTF-8")));
        return this.header("Authorization", value);
    }

    /**
     * 设置http响应体类型
     *
     * @param type FastJson TypeReference
     * @return
     */
    public Request dataType(TypeReference type) {
        this.dataType = type;
        return this;
    }

    /**
     * 设置请求头
     *
     * @param key 键
     * @param value 值
     * @return
     */
    public Request header(String key, String value) {
        if (StringUtils.isNotBlank(key)) {
            headers.add(new BasicHeader(key, value));
        }
        return this;
    }

    /**
     * 设置请求头
     *
     * @param headers 请求头
     * @return
     */
    public Request headers(Map<String, String> headers) {
        if (headers == null || headers.size() == 0) {
            return this;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            this.header(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * 设置请求参数
     *
     * @param key 键
     * @param value 值
     * @return
     */
    public Request param(String key, Object value) {
        if (StringUtils.isNotBlank(key)) {
            params.add(new BasicNameValuePair(key, toSupportedParamValue(value)));
        }
        return this;
    }

    /**
     * 设置请求参数
     *
     * @param params 参数
     * @return
     */
    public Request params(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            return this;
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            this.param(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * 设置请求体
     *
     * @param json 请求体
     * @return
     */
    public Request body(Object json) {
        if (json == null) {
            return this;
        }
        if (json instanceof CharSequence) {
            boolean isValid = JSON.isValid(json.toString());
            if (!isValid) {
                throw new IllegalArgumentException("body format is error:" + json.toString());
            }
        } else if (json instanceof JSONObject) {
            this.json = ((JSONObject) json).toJSONString();
        } else if (json instanceof JSONArray) {
            this.json = ((JSONArray) json).toJSONString();
        } else {
            this.json = JSON.toJSONString(json);
        }
        return this;
    }

    /**
     * 连接配置
     *
     * @param config 连接配置
     * @return
     */
    public Request config(HttpConnConfig config) {
        this.httpConnConfig = config;
        if (this.httpConnConfig == null) {
            this.httpConnConfig = HttpConnConfig.builder().build();
        }
        return this;
    }

    /**
     * 清空请求配置以及参数等
     *
     * @return
     */
    public Request clearRequestAll() {
        this.headers.clear();
        this.params.clear();
        this.json = null;
        this.dataType = null;
        this.httpConnConfig = null;
        return this;
    }

    public Request clearRequestHeaders() {
        this.headers.clear();
        return this;
    }

    public Request clearRequestParams() {
        this.params.clear();
        return this;
    }

    public Request clearRequestBody() {
        this.json = null;
        this.dataType = null;
        return this;
    }

    public Request clearConnectConfig() {
        this.httpConnConfig = null;
        return this;
    }

    /**
     * 执行HTTP 请求
     *
     * @param method 请求方法
     * @return 返回结果
     */
    private R execute(Method method) {
        HttpUriRequest requestMethod = this.switchRequestMethod(method);
        CloseableHttpClient client = this.borrowConnection();
        HttpResponse response = null;
        try {
            response = client.execute(requestMethod);
        } catch (IOException e) {
            IOUtil.close(client);
            throw new CarpIOException(e);
        }
        return this.processResponse(response);
    }

    /**
     * 生成请求方法
     *
     * @param method 请求方法类型
     * @return HttpUriRequest
     */
    private HttpUriRequest switchRequestMethod(Method method) {
        method = (method == null) ? Method.GET : method;
        switch (method) {
            case GET:
                return this.buildHttpUriRequest(RequestBuilder.get());
            case POST:
                return this.buildHttpUriRequest(RequestBuilder.post());
            case PUT:
                return this.buildHttpUriRequest(RequestBuilder.put());
            case DELETE:
                return this.buildHttpUriRequest(RequestBuilder.delete());
            case HEAD:
                return this.buildHttpUriRequest(RequestBuilder.head());
            case PATCH:
                return this.buildHttpUriRequest(RequestBuilder.patch());
            case TRACE:
                return this.buildHttpUriRequest(RequestBuilder.trace());
            case OPTIONS:
                return this.buildHttpUriRequest(RequestBuilder.options());
            default:
                return null;
        }
    }

    /**
     * HttpResponse 转换为自定义 result
     *
     * @param response HttpResponse
     * @return R
     */
    private R processResponse(HttpResponse response) {
        if (response == null) {
            return RUtil.fail();
        }

        int statusCode = Optional.ofNullable(response.getStatusLine())
            .map(StatusLine::getStatusCode).orElse(500);

        R result = new R();
        result.code(statusCode);
        result.msg(Optional.ofNullable(response.getStatusLine()).map(StatusLine::getReasonPhrase).orElse(null));

        if (SUCCESS != statusCode) {
            result.success(false);
        }
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            result.data(null);
            return result;
        }

        String responseString;
        try {
            responseString = EntityUtils.toString(entity, "utf-8");
        } catch (IOException e) {
            throw new CarpIOException(e);
        }
        if (dataType == null || StringUtils.isBlank(responseString)) {
            return result.data(responseString);
        }
        return result.data(JSON.parseObject(responseString, dataType));
    }

    /**
     * 构建请求参数以及连接配置
     *
     * @param builder
     * @return HttpUriRequest
     */
    private HttpUriRequest buildHttpUriRequest(RequestBuilder builder) {
        if (builder == null) {
            return null;
        }
        if (httpConnConfig == null) {
            httpConnConfig = HttpConnConfig.builder().build();
        }
        builder.setUri(url).setConfig(httpConnConfig.getRequestConfig());
        if (params != null && params.size() > 0) {
            params.forEach(nameValuePair -> builder.addParameter(nameValuePair));
        }
        if (headers != null && headers.size() > 0) {
            headers.forEach(nameValuePair -> builder.addHeader(nameValuePair));
        }
        //判断是否是 Application/body 请求
        if (StringUtils.isNotBlank(json)) {
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            builder.setEntity(entity);
        }
        return builder.build();
    }

    /**
     * 获取一个连接
     *
     * @return CloseableHttpClient
     */
    private CloseableHttpClient borrowConnection() {
        return HttpClients.custom().setConnectionManager(httpConnConfig.getConnectionManager()).build();
    }


    /**
     * 将支持的参数类型转换为String
     *
     * @param value 参数值
     * @return String
     */
    private String toSupportedParamValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof CharSequence) {
            return value.toString();
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Byte) {
            return value.toString();
        }
        if (value instanceof Integer) {
            return value.toString();
        }
        if (value instanceof Long) {
            return value.toString();
        }
        if (value instanceof Float) {
            return value.toString();
        }
        if (value instanceof Double) {
            return value.toString();
        }
        throw new IllegalArgumentException(value + "is not supported param type");
    }

}
