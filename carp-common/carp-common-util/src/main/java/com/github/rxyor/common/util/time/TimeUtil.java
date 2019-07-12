package com.github.rxyor.common.util.time;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-24 Fri 16:10:00
 * @since 1.0.0
 */
public class TimeUtil {

    private TimeUtil() {
    }

    public static Long getCurrentSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

}
