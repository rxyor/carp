package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.util.TimeUtil;
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
public class LogDelayJobHandler extends AbstractDelayJobHandler {

    public LogDelayJobHandler() {
    }

    public LogDelayJobHandler(String topic) {
        super(topic);
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
