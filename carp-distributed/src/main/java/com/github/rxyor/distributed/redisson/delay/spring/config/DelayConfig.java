package com.github.rxyor.distributed.redisson.delay.spring.config;

import com.github.rxyor.distributed.redisson.delay.core.DelayGlobalConfig;
import com.github.rxyor.distributed.redisson.delay.core.DelayJobHandler;
import com.github.rxyor.distributed.redisson.delay.core.DelayScanner;
import com.github.rxyor.redis.redisson.codec.FastJsonCodec;
import com.github.rxyor.redis.redisson.util.RedissonUtil;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-29 Wed 14:20:00
 * @since 1.0.0
 */
public class DelayConfig {

    @Getter
    @Setter
    private RedisConnConfig redisConnConfig;

    @Getter
    @Setter
    private String appName;

    @Getter
    private Config redissonConfig;

    @Getter
    @Setter
    private List<DelayJobHandler> handlerList;

    private DelayScanner delayScanner;


    public void init() {
        Objects.requireNonNull(redisConnConfig, "redisConnConfig cant't be null");
        int database = redisConnConfig.getDatabase() == null ? 0 : redisConnConfig.getDatabase();

        Config config = new Config();
        config.setCodec(new FastJsonCodec());
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(redisConnConfig.address());
        singleServerConfig.setDatabase(database);
        if (StringUtils.isNotEmpty(redisConnConfig.getPassword())) {
            singleServerConfig.setPassword(redisConnConfig.getPassword());
        }
        redissonConfig = config;

        RedissonUtil.setConfig(config);
        if (StringUtils.isNotEmpty(this.appName)) {
            DelayGlobalConfig.setAppName(this.appName);
        }

        if (delayScanner == null) {
            delayScanner = new DelayScanner();
        }
        if (handlerList != null && handlerList.size() > 0) {
            handlerList.forEach(handler -> delayScanner.addHandler(handler));
        }
        delayScanner.startup();
    }

    public void destroy() {
        if (delayScanner != null) {
            delayScanner.shutDown();
        }
    }

}
