package com.github.rxyor.common.util.common;

import java.util.Objects;
import lombok.Getter;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-21 Tue 14:31:00
 * @since 1.0.0
 */
public class SnowFlakeUtil {

    @Getter
    private static Long datacenterId = 0L;
    @Getter
    private static Long machineId = 0L;
    private static SnowFlake snowFlake;

    static {
        snowFlake = new SnowFlake(datacenterId, machineId);
    }

    public static void reset(Long datacenterId, Long machineId) {
        Objects.requireNonNull(datacenterId, "datacenterId can't be nul");
        Objects.requireNonNull(machineId, "machineId can't be nul");
        SnowFlakeUtil.datacenterId = datacenterId;
        SnowFlakeUtil.machineId = machineId;
        SnowFlakeUtil.snowFlake = new SnowFlake(datacenterId, machineId);
    }


    public static Long nextId() {
        return snowFlake.nextId();
    }

    public static String nextStringId() {
        return Long.toString(snowFlake.nextId());
    }

    public static String nextHexId() {
        return Long.toHexString(nextId());
    }


    public static void main(String[] args) {

        for (int i = 0; i < (1 << 12); i++) {
            System.out.println(SnowFlakeUtil.nextHexId());
        }

    }

}
