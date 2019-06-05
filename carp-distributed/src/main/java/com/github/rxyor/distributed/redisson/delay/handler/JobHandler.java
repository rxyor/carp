package com.github.rxyor.distributed.redisson.delay.handler;


import com.github.rxyor.distributed.redisson.delay.core.DelayClientProxy;
import com.github.rxyor.distributed.redisson.delay.core.DelayJob;
import com.github.rxyor.distributed.redisson.delay.core.DelayResult;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-21 Tue 13:56:00
 * @since 1.0.0
 */
public interface JobHandler {

    /**
     * handle DelayJob
     * @param delayJob
     * @return DelayResult
     */
    DelayResult consume(DelayJob delayJob);

    /**
     * handler id
     *
     * @return handler id
     */
    String getId();

    /**
     * handler可以处理的topic
     *
     * @return topic
     */
    String getTopic();

    /**
     * 延时任务操作客户端对象
     * @return
     */
    DelayClientProxy getDelayClientProxy();

    /**
     * 延时任务操作客户端对象
     * @return
     */
    void setDelayClientProxy(DelayClientProxy delayClientProxy);
}
