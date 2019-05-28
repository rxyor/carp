package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.redis.redisson.util.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-27 Mon 15:24:00
 * @since 1.0.0
 */
@Slf4j
public class BaseTest {

    @Before
    public void config() {
        RedissonUtil.configFromYaml(DelayQueue.class, "/redis.yml");
    }
}
