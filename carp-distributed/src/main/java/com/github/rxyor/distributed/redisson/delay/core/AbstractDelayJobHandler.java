package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.util.SnowFlake;
import com.github.rxyor.redis.redisson.util.RedissonUtil;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-21 Tue 14:04:00
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractDelayJobHandler implements DelayJobHandler {

    @Getter
    public String topic;

    private static SnowFlake snowFlake = new SnowFlake(5L, 1L);

    public AbstractDelayJobHandler() {
    }

    public AbstractDelayJobHandler(String topic) {
        DelayValidUtil.validateTopic(topic);
        this.topic = topic;
    }

    public void setTopic(String topic) {
        DelayValidUtil.validateTopic(topic);
        this.topic = topic;
    }

    /**
     *  处理任务
     * @param delayJob
     * @return
     */
    protected abstract DelayResult handleDelayJob(DelayJob delayJob);

    /**
     * 消费任务
     *
     * @param delayJob 任务详情
     * @return 消费结果
     */
    @Override
    public DelayResult consume(DelayJob delayJob) {
        DelayResult result;
        try {
            result = this.handleDelayJob(delayJob);
        } catch (Exception e) {
            log.error("handle DelayJob:{} , fail:{}", delayJob, e);
            result = DelayResult.FAIL;
        }
        processResult(result, delayJob);
        return result;
    }

    /**
     * 消费不成功后置处理
     *
     * @param result 消费结果
     * @param delayJob 任务详情
     */
    private void processResult(DelayResult result, DelayJob delayJob) {
        if (result == null) {
            return;
        }
        switch (result) {
            case FAIL:
                recordFailJob(delayJob);
                break;
            case LATER:
                tryPushAgain(delayJob);
                break;
            default:
                break;
        }
    }

    /**
     * 重新放到任务池
     * @param delayJob 任务详情
     */
    private void tryPushAgain(DelayJob delayJob) {
        if (delayJob == null
            || delayJob.getId() == null
            || delayJob.getRetryDelay() == null
            || delayJob.getRetryTimes() == null
            || delayJob.getRetryDelay() <= 0L
            || delayJob.getRetryTimes() <= 0) {
            return;
        }
        DelayBucket.offer(delayJob.getTopic(), delayJob.getRetryDelay(),
            delayJob.getRetryTimes() - 1, delayJob.getRetryDelay(), delayJob.getBody());
    }

    /**
     * 记录消费失败的任务
     *
     * @param delayJob 任务详情
     */
    private void recordFailJob(DelayJob delayJob) {
        if (delayJob == null || delayJob.getId() == null) {
            return;
        }
        RedissonClient client = RedissonUtil.ifNullCreateRedissonClient();
        String key = DelayGlobalConfig.gainFailDelayJobKey() + ":" + delayJob.getId();
        RSet<DelayJob> rSet = client.getSet(key);
        rSet.addAsync(delayJob);
        rSet.expireAsync(30L, TimeUnit.DAYS);
    }

    /**
     * handler id
     *
     * @return Hex Id
     */
    @Override
    public String getId() {
        return snowFlake.nextHexId();
    }
}
