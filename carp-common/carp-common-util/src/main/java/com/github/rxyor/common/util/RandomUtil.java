package com.github.rxyor.common.util;

import java.util.UUID;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-15 Wed 09:52:00
 * @since 1.0.0
 */
public class RandomUtil {

    public static String createUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
