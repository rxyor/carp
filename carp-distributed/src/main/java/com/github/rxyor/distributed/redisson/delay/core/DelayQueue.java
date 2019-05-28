package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.util.TimeUtil;
import com.github.rxyor.redis.redisson.util.RedissonUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-24 Fri 15:20:00
 * @since 1.0.0
 */
public class DelayQueue {

    /**
     * 添加任务到延时队列
     *
     * @param jobId 任务ID
     * @param execTime 执行时间
     */
    public static void offer(Long jobId, Long execTime) {
        DelayValidUtil.validateJobId(jobId);
        DelayValidUtil.validateDelaySeconds(execTime);
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        RScoredSortedSet<DelayScoredItem> rScoredSortedSet = redissonClient
            .getScoredSortedSet(DelayGlobalConfig.computeBucketKey(jobId));
        rScoredSortedSet.add(execTime, new DelayScoredItem(jobId, execTime));
    }

    /**
     * 取出当前所有任务项
     *
     * @param bucketIndex 任务桶标识
     * @return DelayScoredItem List
     */
    public static List<DelayScoredItem> popsNow(Integer bucketIndex) {
        return popsByTime(bucketIndex, 0L, TimeUtil.getCurrentSeconds());
    }

    /**
     * 取出当前指定任务范围的任务项
     *
     * @param bucketIndex 任务桶标识
     * @param startTimeSecond 任务执行时间-始
     * @param endTimeSecond 任务执行时间-末
     * @return DelayScoredItem List
     */
    public static List<DelayScoredItem> popsByTime(Integer bucketIndex, Long startTimeSecond, Long endTimeSecond) {
        List<DelayScoredItem> delayScoredItemList = new ArrayList<>(64);
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        String bucketKey = DelayGlobalConfig.gainBucketKeyByIndex(bucketIndex);
        RScoredSortedSet<DelayScoredItem> rScoredSortedSet = redissonClient
            .getScoredSortedSet(bucketKey);
        Collection<ScoredEntry<DelayScoredItem>> scoredEntries = rScoredSortedSet
            .entryRange(startTimeSecond, true, endTimeSecond, true);
        for (ScoredEntry<DelayScoredItem> entry : scoredEntries) {
            if (entry.getValue() == null) {
                continue;
            }
            delayScoredItemList.add(entry.getValue());
        }
        if (!delayScoredItemList.isEmpty()) {
            rScoredSortedSet.removeRangeByScore(startTimeSecond, true, endTimeSecond, true);
        }
        return delayScoredItemList;
    }

    /**
     * 把就绪任务放到就绪队列
     */
    public static void pushToReady() {
        List<DelayScoredItem> allReadyItemList = new ArrayList<>(16);
        for (int i = 0; i < DelayGlobalConfig.getBuckets(); i++) {
            List<DelayScoredItem> items = popsNow(i);
            allReadyItemList.addAll(items);
        }
        for (DelayScoredItem item : allReadyItemList) {
            Long jobId = Optional.ofNullable(item).map(DelayScoredItem::getId).orElse(null);
            if (jobId == null) {
                continue;
            }
            DelayJob delayJob = DelayJobPool.get(jobId);
            if (delayJob != null) {
                ReadyQueue.offer(delayJob.getTopic(), delayJob.getId());
            }
        }
    }


}
