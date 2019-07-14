package com.github.rxyor.common.core.exception;


import lombok.Getter;
import lombok.ToString;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-15 Wed 17:13:00
 * @since 1.0.0
 */
@ToString(callSuper = true)
public class RedisException extends RuntimeException {

    static final long serialVersionUID = -2746474719155039061L;

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
