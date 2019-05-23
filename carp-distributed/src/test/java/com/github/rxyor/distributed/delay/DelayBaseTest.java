package com.github.rxyor.distributed.delay;

import com.github.rxyor.redis.lettuce.config.RedisConnectionProperties;
import com.github.rxyor.redis.lettuce.config.RedisConnectionProperties.Pool;
import java.time.Duration;
import org.junit.Before;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-22 Wed 18:32:00
 * @since 1.0.0
 */
public class DelayBaseTest {

    @Before
    public void initConnectionProperties() {
        RedisConnectionProperties redisConnectionProperties = new RedisConnectionProperties();
        redisConnectionProperties.setHost("127.0.0.1");
        redisConnectionProperties.setDatabase(0);
        redisConnectionProperties.setPort(6379);
        redisConnectionProperties.setTimeout(Duration.ofSeconds(180L));
        redisConnectionProperties.setPool(new Pool());

        DelayGlobalConfig.setRedisConnectionProperties(redisConnectionProperties);
        DelayGlobalConfig.setAppName("carp");
        DelayGlobalConfig.setScanThreadNum(1);
        DelayGlobalConfig.setProcessJobThreadNum(3);
    }

}
