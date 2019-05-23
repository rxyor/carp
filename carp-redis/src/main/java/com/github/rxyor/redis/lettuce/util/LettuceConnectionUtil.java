package com.github.rxyor.redis.lettuce.util;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.extern.slf4j.Slf4j;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-15 Wed 10:38:00
 * @since 1.0.0
 */
@Slf4j
public class LettuceConnectionUtil {

    private LettuceConnectionUtil() {
    }

    public static StatefulRedisConnection<String, String> getConnection(RedisClient client) {
        if (client == null) {
            return null;
        }
        return client.connect();
    }

    public static void releaseConnection(StatefulRedisConnection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            log.error("release redis connection error:", e);
        }
    }
}
