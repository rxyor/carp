package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.distributed.redisson.delay.config.DelayConfig;
import com.github.rxyor.distributed.redisson.delay.handler.JobHandler;
import com.github.rxyor.redis.redisson.config.RedisConfig;
import com.github.rxyor.redis.redisson.factory.CarpRedissonFactory;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-05 Wed 10:32:00
 * @since 1.0.0
 */
public class ScanWrapper {

    @Getter
    @Setter
    private RedisConfig redisConfig;

    @Getter
    @Setter
    private DelayConfig delayConfig;

    @Getter
    @Setter
    private List<JobHandler> handlerList;

    @Getter
    private RedissonClient redissonClient;

    @Getter
    private DelayClientProxy delayClientProxy;

    @Getter
    private Scanner scanner;

    public void initAndScan() {
        prepare();
        scanner.startup();
    }

    public void destroy() {
        scanner.shutDown();
    }

    public void prepare() {
        Optional.ofNullable(redisConfig)
            .orElseThrow(() -> new IllegalArgumentException("redisConfig can't be null"));
        if (handlerList == null || handlerList.size() == 0) {
            throw new IllegalArgumentException("scanWrapper must have one handler at least");
        }
        if (delayConfig == null) {
            delayConfig = new DelayConfig();
        }
        CarpRedissonFactory factory = CarpRedissonFactory.builder().redisConfig(redisConfig).build();
        redissonClient = factory.createRedissonClient();
        delayClientProxy = new DelayClientProxy(redissonClient, delayConfig);

        scanner = new Scanner(delayClientProxy);
        for (JobHandler handler : handlerList) {
            if (StringUtils.isBlank(handler.getTopic())) {
                continue;
            }
            if (handler.getDelayClientProxy() == null) {
                handler.setDelayClientProxy(delayClientProxy);
            }
            scanner.addHandler(handler);
        }
    }

}
