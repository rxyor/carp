package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.redis.redisson.util.RedissonUtil;
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
public class ReadyQueue {

    /**
     * 添加就绪任务
     *
     * @param topic 任务类型
     * @param delayJobId 任务ID
     */
    public static void offer(String topic, Long delayJobId) {
        DelayValidUtil.validateTopic(topic);
        DelayValidUtil.validateJobId(delayJobId);
        RedissonClient client = RedissonUtil.ifNullCreateRedissonClient();
        RBlockingDeque<Long> rBlockingDeque = client.getBlockingDeque(DelayGlobalConfig.gainBlockingQueueKey(topic));
        rBlockingDeque.offer(delayJobId);
    }

    /**
     * 取出一个就绪任务
     *
     * @param topic 任务类型
     * @return 任务ID
     */
    public static Long pop(String topic) {
        DelayValidUtil.validateTopic(topic);
        RedissonClient client = RedissonUtil.ifNullCreateRedissonClient();
        RBlockingDeque<Long> rBlockingDeque = client.getBlockingDeque(DelayGlobalConfig.gainBlockingQueueKey(topic));
        return pop(rBlockingDeque);
    }

    /**
     * 移除一个就绪任务
     *
     * @param topic 任务类型
     * @param jobId 任务ID
     */
    public static void remove(String topic, Long jobId) {
        DelayValidUtil.validateTopic(topic);
        DelayValidUtil.validateJobId(jobId);
        RedissonClient client = RedissonUtil.ifNullCreateRedissonClient();
        RBlockingDeque<Long> rBlockingDeque = client.getBlockingDeque(DelayGlobalConfig.gainBlockingQueueKey(topic));
        rBlockingDeque.remove(jobId);
    }

    private static <T> T pop(RBlockingDeque<T> deque) {
        try {
            if (deque != null) {
                return deque.pop();
            }
        } catch (Exception e) {
        }
        return null;
    }

}
