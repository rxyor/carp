package com.github.rxyor.redis.lettuce.exception;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-14 Tue 16:56:00
 * @since 1.0.0
 */
public class CreateRedisConnectionException extends RedisException {

    static final long serialVersionUID = -692543806067462111L;

    public CreateRedisConnectionException() {
        this("create redis connection exception");
    }

    public CreateRedisConnectionException(String msg) {
        super(msg);
    }

    public CreateRedisConnectionException(Throwable e) {
        super(e);
    }
}
