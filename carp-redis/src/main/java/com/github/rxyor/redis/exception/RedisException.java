package com.github.rxyor.redis.exception;

import lombok.Getter;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-14 Tue 16:57:00
 * @since 1.0.0
 */
public class RedisException extends RuntimeException {

    static final long serialVersionUID = -692543806067462111L;

    @Getter
    protected String msg;

    public RedisException() {
        this("redis exception");
    }

    public RedisException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public RedisException(Throwable e) {
        super(e.getMessage());
        this.msg = e.getMessage();
    }

}
