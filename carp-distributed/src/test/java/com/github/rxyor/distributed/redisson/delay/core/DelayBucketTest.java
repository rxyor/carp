package com.github.rxyor.distributed.redisson.delay.core;

import java.util.List;
import org.junit.Test;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-27 Mon 15:36:00
 * @since 1.0.0
 */
public class DelayBucketTest extends BaseTest {

    @Test
    public void offer() {
        Girl girl = new Girl();
        girl.setAge(19);
        girl.setName("陈悠");
        for (int i = 0; i < 10; i++) {
            DelayBucket.offer("Girl", 0L, girl);
        }
    }

    @Test
    public void popsReady() {
        List<DelayJob> delayBucketList = DelayBucket.popsReady();
        LogDelayJobHandler handler = new LogDelayJobHandler("Girl");
        delayBucketList.forEach(delayJob -> handler.handleDelayJob(delayJob));
    }

    @Test
    public void popReady() {
        DelayJob delayJob = DelayBucket.popReady("Girl");
        LogDelayJobHandler handler = new LogDelayJobHandler("Girl");
        handler.handleDelayJob(delayJob);
    }

}