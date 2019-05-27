package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.util.SnowFlakeUtil;
import com.github.rxyor.common.util.TimeUtil;
import com.github.rxyor.redis.redisson.util.RedissonUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public static <T> void offer(String topic, Long delaySeconds, T body) {
        DelayValidUtil.validateTopic(topic);
        DelayValidUtil.validateDelaySeconds(delaySeconds);
        DelayJob<T> delayJob = new DelayJob<>(SnowFlakeUtil.nextId(), topic, TimeUtil.getCurrentSeconds(), 60 * 30L,
            body);
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        RScoredSortedSet<DelayScoredItem> rScoredSortedSet = redissonClient
            .getScoredSortedSet(DelayGlobalConfig.computeBucketKey(delayJob.getId()));
        rScoredSortedSet.add(delayJob.getExecTime(), new DelayScoredItem(delayJob.getId(), delayJob.getExecTime()));
        DelayJobPool.add(delayJob);
    }

    public static List<DelayJob> pops() {
        RedissonClient redissonClient = RedissonUtil.ifNullCreateRedissonClient();
        List<DelayJob> delayJobList = new ArrayList<>();
        List<DelayScoredItem> delayScoredItemList = new ArrayList<>();
        for (int i = 0; i < DelayGlobalConfig.getBuckets(); i++) {
            String bucketKey = DelayGlobalConfig.gainBucketKeyByIndex(i);
            RScoredSortedSet<DelayScoredItem> rScoredSortedSet = redissonClient
                .getScoredSortedSet(bucketKey);
            Collection<ScoredEntry<DelayScoredItem>> scoredEntries = rScoredSortedSet
                .entryRange(0d, true, TimeUtil.getCurrentSeconds(), true);
            for (ScoredEntry<DelayScoredItem> entry : scoredEntries) {
                if (entry.getValue() == null) {
                    continue;
                }
                delayScoredItemList.add(entry.getValue());
            }
        }
        for (DelayScoredItem item : delayScoredItemList) {
            if (item.getId() == null) {
                continue;
            }
            DelayJob delayJob = DelayJobPool.get(item.getId());
            if (delayJob != null) {
                delayJobList.add(delayJob);
            }
        }
        return delayJobList;
    }
}
