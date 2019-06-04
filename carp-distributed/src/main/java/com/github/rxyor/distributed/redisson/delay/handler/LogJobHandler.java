package com.github.rxyor.distributed.redisson.delay.handler;

import com.github.rxyor.common.util.TimeUtil;
import com.github.rxyor.distributed.redisson.delay.core.DelayJob;
import com.github.rxyor.distributed.redisson.delay.core.DelayResult;
import com.github.rxyor.distributed.redisson.delay.core.DelayClientProxy;
import lombok.extern.slf4j.Slf4j;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-27 Mon 15:01:00
 * @since 1.0.0
 */
@Slf4j
public class LogJobHandler extends AbstractJobHandler {

    public LogJobHandler() {
    }

    public LogJobHandler(String topic,
        DelayClientProxy delayClientProxy) {
        super(topic, delayClientProxy);
    }

    /**
     * handle DelayJob
     * @param delayJob
     */
    @Override
    public DelayResult handleDelayJob(DelayJob delayJob) {
        System.out.println("now:" + TimeUtil.getCurrentSeconds() + ", process '" + this.topic + "' job:" + delayJob);
        log.info("now:{}, process '{}' job:{}", TimeUtil.getCurrentSeconds(), this.topic, delayJob);
        return DelayResult.SUCCESS;
    }
}
