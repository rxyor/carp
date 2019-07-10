package com.github.rxyor.common.util.thread;

import java.util.concurrent.TimeUnit;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-22 Wed 16:14:00
 * @since 1.0.0
 */
public class ThreadUtil {

    public static void sleepSeconds(Long seconds) {
        if (seconds == null || seconds <= 0) {
            return;
        }
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
