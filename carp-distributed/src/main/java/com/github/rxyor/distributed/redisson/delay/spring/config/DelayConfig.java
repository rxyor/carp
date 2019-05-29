package com.github.rxyor.distributed.redisson.delay.spring.config;

import com.github.rxyor.distributed.redisson.delay.core.DelayGlobalConfig;
import com.github.rxyor.distributed.redisson.delay.core.DelayJobHandler;
import com.github.rxyor.distributed.redisson.delay.core.DelayScanner;
import com.github.rxyor.redis.redisson.codec.FastJsonCodec;
import com.github.rxyor.redis.redisson.util.RedissonUtil;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

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
    private String appName;

    @Getter
    @Setter
    private Config redissonConfig;

    @Getter
    @Setter
    private RedissonClient redissonClient;

    @Getter
    @Setter
    private List<DelayJobHandler> handlerList;

    private DelayScanner delayScanner;


    public void init() {
        Config config = new Config(redissonClient.getConfig());
        config.setCodec(new FastJsonCodec());
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
