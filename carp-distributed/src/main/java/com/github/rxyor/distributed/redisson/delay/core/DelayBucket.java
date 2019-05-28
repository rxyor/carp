package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.util.SnowFlakeUtil;
import com.github.rxyor.common.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-27 Mon 11:39:00
 * @since 1.0.0
 */
public class DelayBucket {

    /**
     * 添加延时任务
     *
     * @param topic 任务类型
     * @param delaySeconds 延时时间(单位秒)
     * @param body 消息体
     * @param <T> 消息类型
     */
    public static <T> void offer(String topic, Long delaySeconds, T body) {
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
    public static <T> void offer(String topic, Long delaySeconds, Integer retryTimes, Long retryDelay, T body) {
        DelayValidUtil.validateTopic(topic);
        DelayValidUtil.validateDelaySeconds(delaySeconds);
        retryTimes = (retryTimes == null || retryTimes < 0) ? 0 : retryTimes;
        retryDelay = (retryDelay == null || retryDelay < 0L) ? 0L : retryDelay;

        DelayJob<T> delayJob = new DelayJob<T>(SnowFlakeUtil.nextId(), topic,
            TimeUtil.getCurrentSeconds() + delaySeconds, retryTimes, retryDelay, body);
        DelayQueue.offer(delayJob.getId(), delayJob.getExecTime());
        DelayJobPool.add(delayJob);
    }

    /**
     * 从就绪队列中取任务
     *
     * @param topic 任务类型
     * @param <T> 消息类型
     * @return DelayJob
     */
    public static <T> DelayJob<T> popReady(String topic) {
        DelayValidUtil.validateTopic(topic);
        Long jobId = ReadyQueue.pop(topic);
        DelayJob<T> delayJob = null;
        if (jobId != null) {
            delayJob = DelayJobPool.get(jobId);
            Optional.ofNullable(delayJob).ifPresent(tDelayJob -> DelayJobPool.delete(jobId));
        }
        return delayJob;
    }

    /**
     * 取出当前所有就绪任务
     *
     * @return DelayJob List
     */
    public static List<DelayJob> popsReady() {
        List<DelayJob> allReadyJobList = new ArrayList<>(16);
        List<DelayScoredItem> allReadyItemList = new ArrayList<>(16);
        for (int i = 0; i < DelayGlobalConfig.getBuckets(); i++) {
            List<DelayScoredItem> items = DelayQueue.popsNow(i);
            allReadyItemList.addAll(items);
        }
        for (DelayScoredItem item : allReadyItemList) {
            Long jobId = Optional.ofNullable(item).map(DelayScoredItem::getId).orElse(null);
            if (jobId == null) {
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
     * 从任务池中取出任务并删除
     *
     * @param jobId 任务ID
     * @param <T> 任务类型
     * @return DelayJob
     */
    private static <T> DelayJob<T> getAndRemoveJobFromJobPool(Long jobId) {
        if (jobId == null) {
            return null;
        }
        DelayJob<T> delayJob = DelayJobPool.get(jobId);
        if (delayJob != null) {
            DelayJobPool.delete(jobId);
        }
        return delayJob;
    }

    /**
     * 从就绪队列中删除任务
     *
     * @param topic 任务类型
     * @param jobId 任务ID
     */
    private static void removeFromReadyQueue(String topic, Long jobId) {
        if (jobId == null || StringUtils.isEmpty(topic)) {
            return;
        }
        ReadyQueue.remove(topic, jobId);
    }

}
