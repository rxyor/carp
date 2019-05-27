package com.github.rxyor.redis.core.exception;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-14 Tue 16:56:00
 * @since 1.0.0
 */
public class ReleaseRedisConnectionException extends RedisException {

    static final long serialVersionUID = -692543806067462111L;

    public ReleaseRedisConnectionException() {
        this("release redis connection exception");
    }

    public ReleaseRedisConnectionException(String msg) {
        super(msg);
    }

    public ReleaseRedisConnectionException(Throwable e) {
        super(e);
    }
}
