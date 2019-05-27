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
public class RedissonConfigException extends RedisException {

    static final long serialVersionUID = -1168919161561043717L;

    public RedissonConfigException() {
        this("redisson config exception");
    }

    public RedissonConfigException(String msg) {
        super(msg);
    }

    public RedissonConfigException(Throwable e) {
        super(e);
    }
}
