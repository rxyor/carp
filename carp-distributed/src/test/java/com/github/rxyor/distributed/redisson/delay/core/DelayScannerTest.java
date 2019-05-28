package com.github.rxyor.distributed.redisson.delay.core;

import org.junit.Before;
import org.junit.Test;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-27 Mon 15:19:00
 * @since 1.0.0
 */
public class DelayScannerTest extends BaseTest {

    private DelayScanner delayScanner;

    @Before
    public void init() {
        delayScanner = new DelayScanner();
        delayScanner.addHandler(new LogDelayJobHandler("Girl"));
    }

    @Test
    public void startup() {
        delayScanner.startup();
//        while (true) {
//            Girl girl = new Girl();
//            girl.setAge(19);
//            girl.setName("陈悠");
//            for (int i = 0; i < 100; i++) {
//                DelayBucket.offer("Girl", (i % 10) * 10L + 1L, girl);
//            }
//        }
    }
}