package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.distributed.redisson.delay.config.DelayConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;

/**
 *<p>
 *实测当Queue 元素为空时 ，RBlockingDeque 并不会阻塞当前线程
 *</p>
 *
 * @author liuyang
 * @date 2019-05-24 Fri 17:19:00
 * @since 1.0.0
 */
@Slf4j
public class ReadyQueue extends DelayConfigHolder {

    public ReadyQueue(RedissonClient redissonClient, DelayConfig delayConfig) {
        super(redissonClient, delayConfig);
    }

    /**
     * 添加就绪任务
     *
     * @param topic 任务类型
     * @param delayJobId 任务ID
     */
    public void offer(String topic, String delayJobId) {
        DelayValidUtil.validateTopic(topic);
        DelayValidUtil.validateJobId(delayJobId);
        RedissonClient redissonClient = super.requireNonNullRedissonClient();
        DelayConfig delayConfig = super.requireNonNullKeyConfig();
        RBlockingDeque<String> rBlockingDeque = redissonClient.getBlockingDeque(delayConfig.buildReadyTopicKey(topic));
        rBlockingDeque.offer(delayJobId);
    }

    /**
     * 取出一个就绪任务
     *
     * @param topic 任务类型
     * @return 任务ID
     */
    public String pop(String topic) {
        DelayValidUtil.validateTopic(topic);
        RedissonClient redissonClient = super.requireNonNullRedissonClient();
        DelayConfig delayConfig = super.requireNonNullKeyConfig();
        RBlockingDeque<String> rBlockingDeque = redissonClient.getBlockingDeque(delayConfig.buildReadyTopicKey(topic));
        return pop(rBlockingDeque);
    }

    /**
     * 移除一个就绪任务
     *
     * @param topic 任务类型
     * @param jobId 任务ID
     */
    public void remove(String topic, String jobId) {
        DelayValidUtil.validateTopic(topic);
        DelayValidUtil.validateJobId(jobId);
        RedissonClient redissonClient = super.requireNonNullRedissonClient();
        DelayConfig delayConfig = super.requireNonNullKeyConfig();
        RBlockingDeque<String> rBlockingDeque = redissonClient.getBlockingDeque(delayConfig.buildReadyTopicKey(topic));
        rBlockingDeque.remove(jobId);
    }

    private <T> T pop(RBlockingDeque<T> deque) {
        try {
            if (deque != null) {
                return deque.pop();
            }
        } catch (Exception e) {
        }
        return null;
    }

}
