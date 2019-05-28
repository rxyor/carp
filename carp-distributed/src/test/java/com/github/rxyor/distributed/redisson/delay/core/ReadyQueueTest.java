package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.util.SnowFlakeUtil;
import org.junit.Test;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-27 Mon 17:00:00
 * @since 1.0.0
 */
public class ReadyQueueTest extends BaseTest {

    @Test
    public void offer() {
        ReadyQueue.offer("Girl", SnowFlakeUtil.nextId());
    }
}