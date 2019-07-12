package com.github.rxyor.common.util.httclient;

import com.github.rxyor.common.util.httclient.config.HttpConnConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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

    public enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    private String domain;

    private Integer port;

    private HttpConnConfig httpConnConfig;

    private final List<NameValuePair> params = new ArrayList<>(16);

    private Request(String domain, Integer port) {
        this.domain = domain;
        this.port = port;
    }

    public static Request get(String domain, Integer port) {
        return new Request(domain, port);
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
        if(config==null){

        }
        this.httpConnConfig = config;
        return this;
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
