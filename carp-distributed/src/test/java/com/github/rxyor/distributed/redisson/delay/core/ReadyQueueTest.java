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
 * @date 2019-05-24 Fri 17:35:00
 * @since 1.0.0
 */
public class ReadyQueueTest {

    @Before
    public void config() {
        RedissonUtil.configFromYaml(DelayQueue.class, "/redis.yml");
    }

    @Test
    public void offer() {
        ReadyQueue.offer("Girl", 329345436378726400L);
    }

    @Test
    public void pop() {
        System.out.println(ReadyQueue.pop("Girl"));
    }

}