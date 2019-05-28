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

    public static <T> void offer(String topic, Long delaySeconds, T body) {
        offer(topic, delaySeconds, 0, 0L, body);
    }

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

    private static void removeFromReadyQueue(String topic, Long jobId) {
        if (jobId == null || StringUtils.isEmpty(topic)) {
            return;
        }
        ReadyQueue.remove(topic, jobId);
    }

}
