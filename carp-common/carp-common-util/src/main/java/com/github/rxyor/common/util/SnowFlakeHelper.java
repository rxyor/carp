package com.github.rxyor.common.util;

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
public class SnowFlakeHelper {

    @Getter
    private Long datacenterId;
    @Getter
    private Long machineId;
    private SnowFlake snowFlake;


    private SnowFlakeHelper(Long datacenterId, Long machineId) {
        Objects.requireNonNull(datacenterId, "datacenterId can't be nul");
        Objects.requireNonNull(machineId, "machineId can't be nul");
        this.datacenterId = datacenterId;
        this.machineId = machineId;
        this.snowFlake = new SnowFlake(datacenterId, machineId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long nextId() {
        return snowFlake.nextId();
    }

    public String nextStringId() {
        return Long.toString(snowFlake.nextId());
    }

    public String nextHexId() {
        return Long.toHexString(nextId());
    }

    public static class Builder {

        private Long datacenterId = 0L;
        private Long machineId = 0L;

        public Builder datacenterId(Long datacenterId) {
            this.datacenterId = datacenterId;
            return this;
        }

        public Builder machineId(Long machineId) {
            this.datacenterId = datacenterId;
            return this;
        }

        public SnowFlakeHelper build() {
            return new SnowFlakeHelper(datacenterId, machineId);
        }
    }

    public static void main(String[] args) {
        SnowFlakeHelper snowFlakeHelper = SnowFlakeHelper.builder().build();

        for (int i = 0; i < (1 << 12); i++) {
            System.out.println(snowFlakeHelper.nextHexId());
        }

    }

}
