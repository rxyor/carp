package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.distributed.redisson.delay.config.DelayConfig;
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
public class JobDetailPool extends DelayConfigHolder {

    public JobDetailPool(RedissonClient redissonClient,
        DelayConfig delayConfig) {
        super(redissonClient, delayConfig);
    }

    /**
     * 任务详情添加至任务池中
     *
     * @param delayJob 任务
     * @param <T> 任务泛型
     */
    public <T> void add(DelayJob<T> delayJob) {
        RedissonClient redissonClient = super.requireNonNullRedissonClient();
        DelayConfig delayConfig = super.requireNonNullKeyConfig();
        RMap<String, DelayJob<T>> rMap = redissonClient.getMap(delayConfig.buildJobDetailsKey());
        rMap.put(delayJob.getId(), delayJob);
    }

    /**
     * 从任务池中获取任务详情
     *
     * @param jobId 任务ID
     * @param <T> 任务泛型
     * @return DelayJob
     */
    public <T> DelayJob<T> get(String jobId) {
        RedissonClient redissonClient = super.requireNonNullRedissonClient();
        DelayConfig delayConfig = super.requireNonNullKeyConfig();
        RMap<String, DelayJob<T>> rMap = redissonClient.getMap(delayConfig.buildJobDetailsKey());
        return rMap.get(jobId);
    }

    /**
     * 删除任务
     *
     * @param jobId 任务ID
     */
    public void delete(String jobId) {
        RedissonClient redissonClient = super.requireNonNullRedissonClient();
        DelayConfig delayConfig = super.requireNonNullKeyConfig();
        RMap<String, DelayJob> rMap = redissonClient.getMap(delayConfig.buildJobDetailsKey());
        rMap.remove(jobId);
    }

    /**
     * 清空任务
     */
    public void clear() {
        RedissonClient redissonClient = super.requireNonNullRedissonClient();
        DelayConfig delayConfig = super.requireNonNullKeyConfig();
        RMap<Long, DelayJob> rMap = redissonClient.getMap(delayConfig.buildJobDetailsKey());
        rMap.clear();
    }
}
