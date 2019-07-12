package com.github.rxyor.common.util.httclient.config;

import java.net.InetAddress;
import java.util.Collection;
import lombok.Getter;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-12 Fri 11:12:00
 * @since 1.0.0
 */
public class HttpConnConfig {

    public static final int DEFAULT_MAX_TOTAL = 10;
    public static final int DEFAULT_MAX_PER_ROUTE = 10;

    @Getter
    private final RequestConfig requestConfig;

    @Getter
    private final PoolingHttpClientConnectionManager connectionManager;

    private HttpConnConfig(RequestConfig requestConfig, PoolingHttpClientConnectionManager connectionManager) {
        this.requestConfig = requestConfig;
        this.connectionManager = connectionManager;
    }

    public static Builder build() {
        return new HttpConnConfig.Builder();
    }


    public static class Builder {

        private final RequestConfig.Builder requestBuilder = RequestConfig.custom();
        private final PoolingHttpClientConnectionManager connectionManager = customerConnectionManager();

        public Builder expectContinueEnabled(boolean expectContinueEnabled) {
            requestBuilder.setExpectContinueEnabled(expectContinueEnabled);
            return this;
        }

        public Builder proxy(HttpHost proxy) {
            requestBuilder.setProxy(proxy);
            return this;
        }

        public Builder localAddress(InetAddress localAddress) {
            requestBuilder.setLocalAddress(localAddress);
            return this;
        }

        public Builder cookieSpec(String cookieSpec) {
            requestBuilder.setCookieSpec(cookieSpec);
            return this;
        }

        public Builder redirectsEnabled(boolean redirectsEnabled) {
            requestBuilder.setRedirectsEnabled(redirectsEnabled);
            return this;
        }

        public Builder relativeRedirectsAllowed(boolean relativeRedirectsAllowed) {
            requestBuilder.setRelativeRedirectsAllowed(relativeRedirectsAllowed);
            return this;
        }

        public Builder circularRedirectsAllowed(boolean circularRedirectsAllowed) {
            requestBuilder.setCircularRedirectsAllowed(circularRedirectsAllowed);
            return this;
        }

        public Builder maxRedirects(int maxRedirects) {
            requestBuilder.setMaxRedirects(maxRedirects);
            return this;
        }

        public Builder authenticationEnabled(boolean authenticationEnabled) {
            requestBuilder.setAuthenticationEnabled(authenticationEnabled);
            return this;
        }

        public Builder targetPreferredAuthSchemes(Collection<String> targetPreferredAuthSchemes) {
            requestBuilder.setTargetPreferredAuthSchemes(targetPreferredAuthSchemes);
            return this;
        }

        public Builder proxyPreferredAuthSchemes(Collection<String> proxyPreferredAuthSchemes) {
            requestBuilder.setProxyPreferredAuthSchemes(proxyPreferredAuthSchemes);
            return this;
        }

        public Builder connectionRequestTimeout(int connectionRequestTimeout) {
            requestBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
            return this;
        }

        public Builder connectTimeout(int connectTimeout) {
            requestBuilder.setConnectTimeout(connectTimeout);
            return this;
        }

        public Builder socketTimeout(int socketTimeout) {
            requestBuilder.setSocketTimeout(socketTimeout);
            return this;
        }

        public Builder normalizeUri(boolean normalizeUri) {
            requestBuilder.setNormalizeUri(normalizeUri);
            return this;
        }

        public Builder connectMaxTotal(int maxTotal) {
            connectionManager.setMaxTotal(maxTotal);
            return this;
        }

        public Builder defaultMaxPerRoute(int defaultMaxPerRoute) {
            connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
            return this;
        }

        public HttpConnConfig build() {
            return new HttpConnConfig(requestBuilder.build(), connectionManager);
        }


        private PoolingHttpClientConnectionManager customerConnectionManager() {
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL);
            connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
            return connectionManager;
        }
    }
}
