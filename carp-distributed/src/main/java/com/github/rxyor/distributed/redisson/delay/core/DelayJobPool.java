package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.redis.redisson.util.RedissonUtil;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-24 Fri 16:45:00
 * @since 1.0.0
 */
public class DelayJobPool {

    public static <T> void add(DelayJob<T> delayJob) {
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        RMap<Long, DelayJob<T>> rMap = redissonClient.getMap(DelayGlobalConfig.gainDelayJobPoolKey());
        rMap.put(delayJob.getId(), delayJob);
    }

    public static <T> DelayJob<T> get(Long jobId) {
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        RMap<Long, DelayJob<T>> rMap = redissonClient.getMap(DelayGlobalConfig.gainDelayJobPoolKey());
        return rMap.get(jobId);
    }

    public static void delete(Long jobId) {
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        RMap<Long, DelayJob> rMap = redissonClient.getMap(DelayGlobalConfig.gainDelayJobPoolKey());
        rMap.remove(jobId);
    }

    public static void clear() {
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        RMap<Long, DelayJob> rMap = redissonClient.getMap(DelayGlobalConfig.gainDelayJobPoolKey());
        rMap.clear();
    }
}
