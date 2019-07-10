package com.github.rxyor.distributed.redisson.delay.handler;

import com.github.rxyor.common.util.common.RandomUtil;
import com.github.rxyor.distributed.redisson.delay.core.DelayJob;
import com.github.rxyor.distributed.redisson.delay.core.DelayResult;
import com.github.rxyor.distributed.redisson.delay.core.DelayValidUtil;
import com.github.rxyor.distributed.redisson.delay.core.DelayClientProxy;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
public abstract class AbstractJobHandler implements JobHandler {

    @Getter
    public String topic;

    @Getter
    public DelayClientProxy delayClientProxy;

    public AbstractJobHandler() {
    }

    public AbstractJobHandler(String topic, DelayClientProxy delayClientProxy) {
        Objects.requireNonNull(delayClientProxy, "delayClientProxy can't be null");
        DelayValidUtil.validateTopic(topic);
        this.topic = topic;
        this.delayClientProxy = delayClientProxy;
    }

    public void setTopic(String topic) {
        DelayValidUtil.validateTopic(topic);
        this.topic = topic;
    }

    @Override
    public void setDelayClientProxy(DelayClientProxy delayClientProxy) {
        Objects.requireNonNull(delayClientProxy, "delayClientProxy can't be null");
        this.delayClientProxy = delayClientProxy;
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
        delayClientProxy.offer(delayJob.getTopic(), delayJob.getRetryDelay(),
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
        delayClientProxy.recordFail(delayJob);
    }

    /**
     * handler id
     *
     * @return Hex Id
     */
    @Override
    public String getId() {
        return RandomUtil.shortUuid();
    }
}
