package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.util.date.TimeUtil;
import com.github.rxyor.distributed.redisson.delay.config.DelayConfig;
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
 * @date 2019-06-04 Tue 15:50:00
 * @since 1.0.0
 */
public class WaitQueue extends DelayConfigHolder {

    public WaitQueue(RedissonClient redissonClient, DelayConfig delayConfig) {
        super(redissonClient, delayConfig);
    }

    /**
     * 添加任务到延时队列
     *
     * @param jobId 任务ID
     * @param execTime 执行时间
     */
    public void offer(String jobId, Long execTime) {
        DelayValidUtil.validateJobId(jobId);
        DelayValidUtil.validateDelaySeconds(execTime);
        RedissonClient redissonClient = super.requireNonNullRedissonClient();
        DelayConfig delayConfig = super.requireNonNullKeyConfig();
        RScoredSortedSet<ScoreItem> rScoredSortedSet = redissonClient
            .getScoredSortedSet(delayConfig.buildWaitQueueKey());
        rScoredSortedSet.add(execTime, new ScoreItem(jobId, execTime));
    }

    /**
     * 取出当前所有任务项
     *
     * @return DelayScoredItem List
     */
    public List<ScoreItem> popsNow() {
        return popsByTime(0L, TimeUtil.getCurrentSeconds());
    }

    /**
     * 取出当前指定任务范围的任务项
     *
     * @param startTimeSecond 任务执行时间-始
     * @param endTimeSecond 任务执行时间-末
     * @return ScoreItem List
     */
    public List<ScoreItem> popsByTime(Long startTimeSecond, Long endTimeSecond) {
        List<ScoreItem> scoreItemList = new ArrayList<>(64);
        RedissonClient redissonClient = super.requireNonNullRedissonClient();
        DelayConfig delayConfig = super.requireNonNullKeyConfig();
        RScoredSortedSet<ScoreItem> rScoredSortedSet = redissonClient.getScoredSortedSet(delayConfig.buildWaitQueueKey());
        Collection<ScoredEntry<ScoreItem>> scoredEntries = rScoredSortedSet
            .entryRange(startTimeSecond, true, endTimeSecond, true);
        for (ScoredEntry<ScoreItem> entry : scoredEntries) {
            if (entry.getValue() == null) {
                continue;
            }
            scoreItemList.add(entry.getValue());
        }
        if (!scoreItemList.isEmpty()) {
            rScoredSortedSet.removeRangeByScore(startTimeSecond, true, endTimeSecond, true);
        }
        return scoreItemList;
    }

}
