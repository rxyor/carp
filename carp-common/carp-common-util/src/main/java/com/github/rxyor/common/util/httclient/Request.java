package com.github.rxyor.common.util.httclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.rxyor.common.core.exception.CarpIOException;
import com.github.rxyor.common.core.model.R;
import com.github.rxyor.common.core.util.RUtil;
import com.github.rxyor.common.util.httclient.config.HttpConnConfig;
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

    public static final String HTTP_BASIC_HEADER_KEY = "Authorization";

    public static final int SUCCESS = 200;

    public enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    private String url;

    private TypeReference resultType;

    private HttpConnConfig httpConnConfig;

    private final List<NameValuePair> params = new ArrayList<>(16);
    private final List<Header> headers = new ArrayList<>(16);

    private Request(String url) {
        this.url = url;
    }

    public static Request url(String url) {
        return new Request(url);
    }

    public Request resultType(TypeReference type) {
        this.resultType = type;
        return this;
    }

    public Request httpBasic(String username, String password) {
        if (StringUtils.isBlank(username)) {
            return this;
        }
        String pwd = password == null ? "" : password;
        String crypt = username + ":" + pwd;
        String value = "Basic " + Base64.getEncoder().encodeToString(crypt.getBytes(Charset.forName("UTF-8")));
        return this.header(HTTP_BASIC_HEADER_KEY, value);
    }

    public Request header(String key, String value) {
        if (StringUtils.isNotBlank(key)) {
            headers.add(new BasicHeader(key, value));
        }
        return this;
    }

    public Request headers(Map<String, String> headers) {
        if (headers == null || headers.size() == 0) {
            return this;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            this.header(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Request param(String key, Object value) {
        if (StringUtils.isNotBlank(key)) {
            params.add(new BasicNameValuePair(key, toSupportedValue(value)));
        }
        return this;
    }

    public Request params(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            return this;
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            this.param(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Request config(HttpConnConfig config) {
        this.httpConnConfig = config;
        if (this.httpConnConfig == null) {
            this.httpConnConfig = HttpConnConfig.builder().build();
        }
        return this;
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

    private R execute(Method method) {
        HttpUriRequest requestMethod = this.switchRequestMethod(method);
        CloseableHttpClient client = this.borrowConnection();
        HttpResponse response = null;
        try {
            response = client.execute(requestMethod);

        } catch (IOException e) {
            throw new CarpIOException(e);
        }
        return this.processResponse(response);
    }

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

        String msg = null;
        try {
            msg = EntityUtils.toString(entity, "utf-8");
        } catch (IOException e) {
            throw new CarpIOException(e);
        }
        if (resultType == null || StringUtils.isBlank(msg)) {
            return result.data(msg);
        }
        return result.data(JSON.parseObject(msg, resultType));
    }

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
            default:
                return null;
        }
    }

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
        return builder.build();
    }

    private CloseableHttpClient borrowConnection() {
        return HttpClients.custom().setConnectionManager(httpConnConfig.getConnectionManager()).build();
    }

    private String toSupportedValue(Object value) {
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
