package com.github.rxyor.distributed.lettuce.delay;

import com.alibaba.fastjson.JSONObject;
import com.github.rxyor.redis.lettuce.factory.RedisConnectionFactory;
import io.lettuce.core.Range;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-21 Tue 17:42:00
 * @since 1.0.0
 */
public class RedisClientWrapper {

    @Setter
    public static RedisClient redisClient;

    public static RedisConnectionFactory factory;

    static {
        factory = RedisConnectionFactory.builder()
            .redisConnectionProperties(DelayGlobalConfig.getRedisConnectionProperties()).build();
    }

    public static RedisClient getRedisClient() {
        if (factory == null) {
            factory = RedisConnectionFactory.builder()
                .redisConnectionProperties(DelayGlobalConfig.getRedisConnectionProperties()).build();
        }

        if (redisClient == null) {
            redisClient = factory.createClient();
        }
        return redisClient;
    }

    public static StatefulRedisConnection<String, String> borrowConnection() {
        return factory.borrowConnection();
    }

    public static void returnConnection(StatefulRedisConnection connection) {
        if (connection != null) {
            factory.returnConnection(connection);
        }
    }

    public static void lpush(String key, String... values) {
        if (values == null || values.length == 0) {
            return;
        }
        StatefulRedisConnection<String, String> conn = null;
        try {
            conn = borrowConnection();
            RedisCommands<String, String> commands = conn.sync();
            commands.lpush(key, values);
        } finally {
            returnConnection(conn);
        }
    }

    public static String lpop(String key) {
        StatefulRedisConnection<String, String> conn = null;
        try {
            conn = borrowConnection();
            RedisCommands<String, String> commands = conn.sync();
            return commands.lpop(key);
        } finally {
            returnConnection(conn);
        }
    }


    public static <T> void zaddWithScore(String key, double score, DelayJob<T> delayJob) {
        StatefulRedisConnection<String, String> conn = null;
        try {
            conn = borrowConnection();
            RedisCommands<String, String> commands = conn.sync();
            commands.zadd(key, ScoredValue.just(score, JSONObject.toJSONString(delayJob)));
        } finally {
            returnConnection(conn);
        }
    }

    public static <T> void zremrangeByScore(String key, Long lower, Long upper) {
        StatefulRedisConnection<String, String> conn = null;
        try {
            conn = borrowConnection();
            RedisCommands<String, String> commands = conn.sync();
            commands.zremrangebyscore(key, Range.create(lower, upper));
        } finally {
            returnConnection(conn);
        }
    }

    public static List<String> zrangeByScore(String key) {
        StatefulRedisConnection<String, String> conn = null;
        try {
            conn = borrowConnection();
            RedisCommands<String, String> commands = conn.sync();
            Range<Long> range = Range.create(0L, System.currentTimeMillis() / 1000L);
            List<String> list = commands.zrangebyscore(key, range);
            return list == null ? new ArrayList<>(0) : list;
        } finally {
            returnConnection(conn);
        }
    }

    public static List<String> zrangeByScoreAndZrem(String key) {
        StatefulRedisConnection<String, String> conn = null;
        try {
            conn = borrowConnection();
            RedisCommands<String, String> commands = conn.sync();
            Range<Long> range = Range.create(0L, System.currentTimeMillis() / 1000L);
            List<String> list = commands.zrangebyscore(key, range);
            commands.zremrangebyscore(key, range);
            return list == null ? new ArrayList<>(0) : list;
        } finally {
            returnConnection(conn);
        }
    }
}
