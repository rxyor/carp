package com.github.rxyor.redis.redisson;

import com.github.rxyor.redis.redisson.util.RedissonUtil;
import org.junit.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-23 Thu 18:39:00
 * @since 1.0.0
 */
public class RedissonUtilTest {

    @Test
    public void ifNullCreateClient() {
        RedissonUtil.configFromYaml(RedissonUtil.class, "/redis.yml");
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        RBucket<Girl> bucket = redissonClient.getBucket("Person");
//        bucket.set(new Girl("陈悠", 19));
        System.out.println(bucket.get());
//        redissonClient.getSet("Hello");
    }
}