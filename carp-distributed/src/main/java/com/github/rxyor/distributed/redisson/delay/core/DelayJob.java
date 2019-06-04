package com.github.rxyor.distributed.redisson.delay.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-24 Fri 15:36:00
 * @since 1.0.0
 */
@AllArgsConstructor
@Data
public class DelayJob<T> {

    public DelayJob() {
    }

    /**
     * job id
     */
    private String id;

    /**
     * 消息类型
     */
    private String topic;

    /**
     * 任务执行时间(时间戳:精确到秒)
     */
    private Long execTime;

    /**
     * 重试次数
     */
    private Integer retryTimes = 0;

    /**
     * 消费失败，重新消费间隔(单位秒)
     * 默认0L, 消费失败不重新消费
     */
    private Long retryDelay = 0L;

    /**
     * 消息体
     */
    private T body;

}
