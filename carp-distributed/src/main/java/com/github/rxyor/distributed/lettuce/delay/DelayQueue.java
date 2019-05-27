package com.github.rxyor.distributed.lettuce.delay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.rxyor.common.util.SnowFlakeUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-17 Fri 09:45:00
 * @since 1.0.0
 */
@AllArgsConstructor
public class DelayQueue<T> {

    @Setter
    private String topic;

    public DelayQueue() {
    }

    public void offer(T data, Long delay) {
        if (StringUtils.isEmpty(topic)) {
            throw new IllegalArgumentException("topic can't be empty");
        }
        if (delay == null || delay <= 0L) {
            throw new IllegalArgumentException("delay must >= 0");
        }
        DelayJob<T> delayJob = new DelayJob<>(SnowFlakeUtil.nextHexId(),
            topic, System.currentTimeMillis() / 1000 + delay, data);
        RedisClientWrapper
            .zaddWithScore(DelayGlobalConfig.rebuildWaitQueueTopic(topic), delayJob.getExecTime(), delayJob);
    }

    public List<DelayJob<T>> peek() {
        List<String> list = RedisClientWrapper.zrangeByScoreAndZrem(DelayGlobalConfig.rebuildWaitQueueTopic(topic));
        return convert2DelayJob(list);
    }

    private List<DelayJob<T>> convert2DelayJob(List<String> strings) {
        List<DelayJob<T>> delayJobList = new ArrayList<>(strings.size());
        for (String s : strings) {
            DelayJob<T> delayJob = JSON.parseObject(s, new TypeReference<DelayJob<T>>() {
            });
            delayJobList.add(delayJob);
        }
        return delayJobList;
    }

}
