package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.util.SnowFlakeUtil;
import com.github.rxyor.common.util.TimeUtil;
import org.junit.Test;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-24 Fri 16:55:00
 * @since 1.0.0
 */
public class DelayQueueTest extends BaseTest {

    @Test
    public void offer() {
        Girl girl = new Girl();
        girl.setAge(19);
        girl.setName("陈悠");
        DelayQueue.<Girl>offer(SnowFlakeUtil.nextId(), TimeUtil.getCurrentSeconds() + 300L);
    }
}