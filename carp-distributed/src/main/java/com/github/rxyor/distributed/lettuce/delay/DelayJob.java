package com.github.rxyor.distributed.lettuce.delay;

import lombok.AllArgsConstructor;
import lombok.Data;

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
@Data
public class DelayJob<T> {

    public DelayJob() {
    }

    /**
     * job id
     */
    private String id;

    /**
     * 任务类型(redis key)
     */
    private String topic;

    /**
     * 执行时间
     */
    private Long execTime;

    /**
     * job 内容
     */
    private T body;

}
