package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.util.SnowFlake;
import lombok.Getter;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-21 Tue 14:04:00
 * @since 1.0.0
 */
public abstract class AbstractDelayJobHandler implements DelayJobHandler {

    @Getter
    protected final String topic;

    private static SnowFlake snowFlake = new SnowFlake(5L, 1L);

    public AbstractDelayJobHandler(String topic) {
        this.topic = topic;
    }

    @Override
    public String getId() {
        return snowFlake.nextHexId();
    }
}
