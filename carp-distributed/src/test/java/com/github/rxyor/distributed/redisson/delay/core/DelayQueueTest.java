package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.redis.redisson.util.RedissonUtil;
import org.junit.Before;
import org.junit.Test;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-24 Fri 16:55:00
 * @since 1.0.0
 */
public class DelayQueueTest {

    @Before
    public void config() {
        RedissonUtil.configFromYaml(DelayQueue.class, "/redis.yml");
    }

    @Test
    public void offer() {
        Girl girl = new Girl();
        girl.setAge(19);
        girl.setName("陈悠");

        DelayQueue.<Girl>offer("Girl", 300L, girl);
    }
}