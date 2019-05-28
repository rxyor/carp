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

    /**
     * 任务详情添加至任务池中
     *
     * @param delayJob 任务
     * @param <T> 任务泛型
     */
    public static <T> void add(DelayJob<T> delayJob) {
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        RMap<Long, DelayJob<T>> rMap = redissonClient.getMap(DelayGlobalConfig.gainDelayJobPoolKey());
        rMap.put(delayJob.getId(), delayJob);
    }

    /**
     * 从任务池中取出任务详情
     *
     * @param jobId 任务ID
     * @param <T> 任务泛型
     * @return DelayJob
     */
    public static <T> DelayJob<T> get(Long jobId) {
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        RMap<Long, DelayJob<T>> rMap = redissonClient.getMap(DelayGlobalConfig.gainDelayJobPoolKey());
        return rMap.get(jobId);
    }

    /**
     * 删除任务
     *
     * @param jobId 任务ID
     */
    public static void delete(Long jobId) {
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        RMap<Long, DelayJob> rMap = redissonClient.getMap(DelayGlobalConfig.gainDelayJobPoolKey());
        rMap.remove(jobId);
    }

    /**
     * 清空任务
     */
    public static void clear() {
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        RMap<Long, DelayJob> rMap = redissonClient.getMap(DelayGlobalConfig.gainDelayJobPoolKey());
        rMap.clear();
    }
}
