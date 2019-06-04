package com.github.rxyor.distributed.redisson.delay.core;

import org.apache.commons.lang3.StringUtils;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-24 Fri 17:24:00
 * @since 1.0.0
 */
public class DelayValidUtil {

    public static void validateTopic(String topic) {
        if (StringUtils.isBlank(topic)) {
            throw new IllegalArgumentException("topic can't be blank");
        }
    }

    public static void validateHandlerId(String handlerId) {
        if (StringUtils.isBlank(handlerId)) {
            throw new IllegalArgumentException("handler's id can't be blank");
        }
    }

    public static void validateJobId(Long delayJobId) {
        if (delayJobId == null || delayJobId <= 0L) {
            throw new IllegalArgumentException("delayJobId must > 0");
        }
    }

    public static void validateJobId(String delayJobId) {
        if (StringUtils.isBlank(delayJobId)) {
            throw new IllegalArgumentException("delayJobId can't be blank");
        }
    }

    public static void validateDelaySeconds(Long delaySeconds) {
        if (delaySeconds == null || delaySeconds < 0L) {
            throw new IllegalArgumentException("delaySeconds must >= 0");
        }
    }

    public static void validateExecTime(Long execTime) {
        if (execTime == null || execTime < 0L) {
            throw new IllegalArgumentException("execTime must >= 0");
        }
    }

}
