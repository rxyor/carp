package com.github.rxyor.common.util.httpclient;

import java.net.InetAddress;
import java.util.Collection;
import lombok.Builder;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;

/**
 *<p>
 *连接配置
 *</p>
 *
 * @author liuyang
 * @date 2019-07-10 Wed 16:26:00
 * @since 1.0.0
 */
@Builder(toBuilder = true)
public class ConnectionConfig {

    public final static long DEFAULT_CONNECT_TIMEOUT = 5000L;
    public final static long DEFAULT_SOCKET_TIMEOUT = 15000L;
    public final static long DEFAULT_CONNECTION_REQUEST_TIMOUT = 5000L;

    private final boolean expectContinueEnabled;
    private final HttpHost proxy;
    private final InetAddress localAddress;
    private final boolean staleConnectionCheckEnabled;
    private final String cookieSpec;
    private final boolean redirectsEnabled;
    private final boolean relativeRedirectsAllowed;
    private final boolean circularRedirectsAllowed;
    private final int maxRedirects;
    private final boolean authenticationEnabled;
    private final Collection<String> targetPreferredAuthSchemes;
    private final Collection<String> proxyPreferredAuthSchemes;
    private final int connectionRequestTimeout;
    private final int connectTimeout;
    private final int socketTimeout;
    private final boolean contentCompressionEnabled;
    private final boolean normalizeUri;

    private final RequestConfig requestConfig;

    private final HttpClientConnectionManager connectionManager;

    public static class Builder {

    }

}
