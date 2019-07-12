package com.github.rxyor.common.util.lang;

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

    public static String shortUuid() {
        UUID uuid = UUID.randomUUID();
        StringBuilder sb = new StringBuilder();
        sb.append(digits(uuid.getMostSignificantBits() >> 32, 8));
        sb.append(digits(uuid.getMostSignificantBits() >> 16, 4));
        sb.append(digits(uuid.getMostSignificantBits(), 4));
        sb.append(digits(uuid.getLeastSignificantBits() >> 48, 4));
        sb.append(digits(uuid.getLeastSignificantBits(), 12));
        return sb.toString();
    }

    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return RadixUtil.convert2String(hi | (val & (hi - 1)), RadixUtil.MAX_RADIX).substring(1);
    }

}
