package com.github.rxyor.redis.redisson.util;

import com.github.rxyor.common.util.FileUtil;
import com.github.rxyor.redis.core.exception.RedissonConfigException;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-23 Thu 15:12:00
 * @since 1.0.0
 */
public class RedissonUtil {

    @Getter
    private static Config config;

    @Getter
    private static RedissonClient redissonClient;

    public static void setConfig(Config config) {
        Objects.requireNonNull(config, "config can't be null");
        RedissonUtil.config = config;
    }

    public static void setRedissonClient(RedissonClient redissonClient) {
        Objects.requireNonNull(redissonClient, "redissonClient can't be null");
        RedissonUtil.redissonClient = redissonClient;
    }


    public static void configFromYaml(Class currentPackageClass, String resourcePath) {
        try {
            config = Config.fromYAML(FileUtil.readTextFromResource(currentPackageClass, resourcePath));
        } catch (IOException e) {
            throw new RedissonConfigException(e);
        }
    }

    public static void configFromJson(Class currentPackageClass, String resourcePath) {
        try {
            config = Config.fromJSON(FileUtil.readTextFromResource(currentPackageClass, resourcePath));
        } catch (IOException e) {
            throw new RedissonConfigException(e);
        }
    }

    public static RedissonClient ifNullCreateRedissonClient() {
        if (redissonClient == null) {
            redissonClient = createRedissonClient();
        }
        return redissonClient;
    }

    public static RedissonClient createRedissonClient() {
        Optional.ofNullable(config).orElseThrow(() -> new RedissonConfigException("redisson config not set"));
        return Redisson.create(config);
    }

    public static void shutDown() {
        if (redissonClient != null && !redissonClient.isShutdown()) {
            redissonClient.shutdown();
        }
    }

}
