package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.util.common.RandomUtil;
import com.github.rxyor.common.util.date.TimeUtil;
import com.github.rxyor.distributed.redisson.delay.config.DelayConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-04 Tue 18:25:00
 * @since 1.0.0
 */
public class DelayClientProxy {

    @Getter
    private RedissonClient redissonClient;

    @Getter
    @Setter
    private DelayConfig delayConfig;

    @Getter
    private final WaitQueue waitQueue;

    @Getter
    private final ReadyQueue readyQueue;

    @Getter
    private final JobDetailPool jobDetailPool;


    public DelayClientProxy(RedissonClient redissonClient,
        DelayConfig delayConfig) {
        Objects.requireNonNull(redissonClient, "redissonClient can't be null");
        Objects.requireNonNull(delayConfig, "delayConfig can't be null");
        this.redissonClient = redissonClient;
        this.delayConfig = delayConfig;
        this.waitQueue = new WaitQueue(redissonClient, delayConfig);
        this.readyQueue = new ReadyQueue(redissonClient, delayConfig);
        this.jobDetailPool = new JobDetailPool(redissonClient, delayConfig);
    }


    /**
     * 添加延时任务
     *
     * @param topic 任务类型
     * @param delaySeconds 延时时间(单位秒)
     * @param body 消息体
     * @param <T> 消息类型
     */
    public <T> void offer(String topic, Long delaySeconds, T body) {
        offer(topic, delaySeconds, 0, 0L, body);
    }

    /**
     * 添加延时任务
     *
     * @param topic 任务类型
     * @param delaySeconds 延时时间(单位秒)
     * @param retryTimes 重试次数
     * @param retryDelay 重试间隔
     * @param body 消息体
     * @param <T> 消息类型
     */
    public <T> void offer(String topic, Long delaySeconds, Integer retryTimes, Long retryDelay, T body) {
        DelayJob<T> delayJob = new DelayJob<T>(RandomUtil.shortUuid(), topic,
            TimeUtil.getCurrentSeconds() + delaySeconds, retryTimes, retryDelay, body);
        offer(delayJob);
    }

    /**
     * 添加延时任务
     *
     * @param delayJob 任务
     * @param <T> 消息类型
     */
    public <T> void offer(DelayJob<T> delayJob) {
        Optional.ofNullable(delayJob).orElseThrow(() -> new IllegalArgumentException("delayJob can't be null"));
        DelayValidUtil.validateTopic(delayJob.getTopic());
        DelayValidUtil.validateExecTime(delayJob.getExecTime());
        if (delayJob.getRetryTimes() == null || delayJob.getRetryTimes() < 0) {
            delayJob.setRetryTimes(0);
        }
        if (delayJob.getRetryDelay() == null || delayJob.getRetryDelay() < 0) {
            delayJob.setRetryDelay(0L);
        }
        if (StringUtils.isBlank(delayJob.getId())) {
            delayJob.setId(RandomUtil.shortUuid());
        }
        waitQueue.offer(delayJob.getId(), delayJob.getExecTime());
        jobDetailPool.add(delayJob);
    }

    /**
     * 从就绪队列中取任务
     *
     * @param topic 任务类型
     * @param <T> 消息类型
     * @return DelayJob
     */
    public <T> DelayJob<T> popReadyJob(String topic) {
        DelayValidUtil.validateTopic(topic);
        String jobId = readyQueue.pop(topic);
        DelayJob<T> delayJob = null;
        if (jobId != null) {
            delayJob = jobDetailPool.get(jobId);
            Optional.ofNullable(delayJob).ifPresent(tDelayJob -> jobDetailPool.delete(jobId));
        }
        return delayJob;
    }

    /**
     * 取出当前所有就绪任务
     *
     * @return DelayJob List
     */
    public List<DelayJob> popsReadyJob() {
        List<DelayJob> allReadyJobList = new ArrayList<>(16);
        List<ScoreItem> scoreItemList = waitQueue.popsNow();
        for (ScoreItem item : scoreItemList) {
            String jobId = Optional.ofNullable(item).map(ScoreItem::getId).orElse(null);
            if (StringUtils.isEmpty(jobId)) {
                continue;
            }
            DelayJob delayJob = getAndRemoveJobFromJobPool(jobId);
            if (delayJob != null) {
                removeFromReadyQueue(delayJob.getTopic(), delayJob.getId());
                allReadyJobList.add(delayJob);
            }
        }
        return allReadyJobList;
    }

    /**
     * 把就绪任务放到就绪队列
     */
    public void popsNowAndPushToReady() {
        List<ScoreItem> scoreItemList = waitQueue.popsNow();
        for (ScoreItem item : scoreItemList) {
            String jobId = Optional.ofNullable(item).map(ScoreItem::getId).orElse(null);
            if (StringUtils.isEmpty(jobId)) {
                continue;
            }
            DelayJob delayJob = jobDetailPool.get(jobId);
            if (delayJob != null) {
                readyQueue.offer(delayJob.getTopic(), delayJob.getId());
            }
        }
    }

    /**
     * 添加延时任务
     *
     * @param delayJob 任务
     * @param <T> 消息类型
     */
    public <T> void recordFail(DelayJob<T> delayJob) {
        if (delayJob == null || StringUtils.isBlank(delayJob.getId())) {
            return;
        }
        RSet<DelayJob> rSet = redissonClient.getSet(delayConfig.buildFailJobsKey(delayJob.getId()));
        rSet.addAsync(delayJob);
        rSet.expireAsync(30L, TimeUnit.DAYS);
    }

    /**
     * 从任务池中取出任务并删除
     *
     * @param jobId 任务ID
     * @param <T> 任务类型
     * @return DelayJob
     */
    private <T> DelayJob<T> getAndRemoveJobFromJobPool(String jobId) {
        if (StringUtils.isBlank(jobId)) {
            return null;
        }
        DelayJob<T> delayJob = jobDetailPool.get(jobId);
        if (delayJob != null) {
            jobDetailPool.delete(jobId);
        }
        return delayJob;
    }

    /**
     * 从就绪队列中删除任务
     *
     * @param topic 任务类型
     * @param jobId 任务ID
     */
    private void removeFromReadyQueue(String topic, String jobId) {
        if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(jobId)) {
            return;
        }
        readyQueue.remove(topic, jobId);
    }

}
