package com.github.rxyor.example.spring.mvc.delayqueue.config;

import com.github.rxyor.distributed.redisson.delay.core.DelayClientProxy;
import com.github.rxyor.distributed.redisson.delay.core.ScanWrapper;
import com.github.rxyor.distributed.redisson.delay.handler.JobHandler;
import com.github.rxyor.distributed.redisson.delay.handler.LogJobHandler;
import com.github.rxyor.redis.redisson.config.RedisConfig;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-15 Mon 16:23:00
 * @since 1.0.0
 */
@Configuration
public class DelayQueueConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer port;

    @Value("${spring.redis.database}")
    private Integer database;

    @Value("${spring.redis.password}:''")
    private String redisPassword;

    @Bean
    public RedisConfig redisConfig() {
        RedisConfig redisConfig = new RedisConfig();
        redisConfig.setHost(redisHost);
        redisConfig.setPort(port);
        redisConfig.setPassword(redisPassword);
        redisConfig.setDatabase(database);
        return redisConfig;
    }

    @Bean
    public com.github.rxyor.distributed.redisson.delay.config.DelayConfig delayConfig() {
        com.github.rxyor.distributed.redisson.delay.config.DelayConfig delayConfig = new com.github.rxyor.distributed.redisson.delay.config.DelayConfig();
        delayConfig.setAppName("carp-boot");
        return delayConfig;
    }

    @Bean
    public List<JobHandler> handlerList() {
        List<JobHandler> handlerList = new ArrayList<>(4);
        LogJobHandler logJobHandler = new LogJobHandler();
        logJobHandler.setTopic("girl");
        handlerList.add(logJobHandler);
        return handlerList;
    }

    @Bean(initMethod = "initAndScan", destroyMethod = "destroy")
    public ScanWrapper scanWrapper() {
        ScanWrapper scanWrapper = new ScanWrapper();
        scanWrapper.setRedisConfig(redisConfig());
        scanWrapper.setDelayConfig(delayConfig());
        scanWrapper.setHandlerList(handlerList());
        return scanWrapper;
    }

    @Bean
    public DelayClientProxy delayClientProxy() {
        return scanWrapper().getDelayClientProxy();
    }

}
