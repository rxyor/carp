package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.redis.redisson.util.RedissonUtil;
import java.util.Optional;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-24 Fri 17:19:00
 * @since 1.0.0
 */
public class ReadyQueue {

    public static void offer(String topic, Long delayJobId) {
        DelayValidUtil.validateTopic(topic);
        DelayValidUtil.validateJobId(delayJobId);
        RedissonClient client = RedissonUtil.ifNullCreateRedissonClient();
        RBlockingDeque<Long> rBlockingDeque = client.getBlockingDeque(DelayGlobalConfig.gainBlockingQueueKey(topic));
        rBlockingDeque.offer(delayJobId);
    }

    public static <T> DelayJob<T> pop(String topic) {
        DelayValidUtil.validateTopic(topic);
        RedissonClient client = RedissonUtil.ifNullCreateRedissonClient();
        RBlockingDeque<Long> rBlockingDeque = client.getBlockingDeque(DelayGlobalConfig.gainBlockingQueueKey(topic));
        Long jobId = rBlockingDeque.pop();
        DelayJob<T> delayJob = null;
        if (jobId != null) {
            delayJob = DelayJobPool.get(jobId);
            Optional.ofNullable(delayJob).ifPresent(tDelayJob -> DelayJobPool.delete(jobId));
        }
        return delayJob;
    }

}
