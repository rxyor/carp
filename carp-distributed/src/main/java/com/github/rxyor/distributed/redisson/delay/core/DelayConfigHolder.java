package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.distributed.redisson.delay.config.DelayConfig;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.redisson.api.RedissonClient;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-04 Tue 16:40:00
 * @since 1.0.0
 */
public class DelayConfigHolder {

    @Getter
    @Setter
    private RedissonClient redissonClient;

    @Getter
    @Setter
    private DelayConfig delayConfig;

    public DelayConfigHolder(RedissonClient redissonClient,
        DelayConfig delayConfig) {
        Objects.requireNonNull(redissonClient, "redissonClient can't be null");
        Objects.requireNonNull(delayConfig, "delayConfig can't be null");
        this.redissonClient = redissonClient;
        this.delayConfig = delayConfig;
    }

    protected RedissonClient requireNonNullRedissonClient() {
        Objects.requireNonNull(redissonClient, "redissonClient is null , you must set a non null redissonClient");
        return this.redissonClient;
    }

    protected DelayConfig requireNonNullKeyConfig() {
        if (delayConfig == null) {
            delayConfig = new DelayConfig();
        }
        return delayConfig;
    }

}
