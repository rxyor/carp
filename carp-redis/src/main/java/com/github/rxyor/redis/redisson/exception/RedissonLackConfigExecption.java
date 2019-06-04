package com.github.rxyor.redis.redisson.exception;

import com.github.rxyor.common.core.exception.RedisException;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-04 Tue 13:50:00
 * @since 1.0.0
 */
public class RedissonLackConfigExecption extends RedisException {

    static final long serialVersionUID = -1826914266774839294L;

    public RedissonLackConfigExecption() {
        this("redis lack config exception");
    }

    public RedissonLackConfigExecption(String msg) {
        super(msg);
    }

    public RedissonLackConfigExecption(Throwable e) {
        super(e);
    }
}
