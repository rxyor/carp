package com.github.rxyor.redis.factory;

import com.github.rxyor.redis.config.RedisConnectionProperties;
import com.github.rxyor.redis.config.RedisConnectionProperties.Pool;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.time.Duration;
import org.junit.Before;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-14 Tue 17:23:00
 * @since 1.0.0
 */
public class RedisConnectionFactoryTest {

    private RedisConnectionProperties redisConnectionProperties;

    @Before
    public void initConnectionProperties() {
        redisConnectionProperties = new RedisConnectionProperties();
        redisConnectionProperties.setHost("127.0.0.1");
        redisConnectionProperties.setDatabase(0);
        redisConnectionProperties.setPort(6379);
        redisConnectionProperties.setTimeout(Duration.ofSeconds(180L));
        redisConnectionProperties.setPool(new Pool());
    }

    @org.junit.Test
    public void createClient() {
        RedisConnectionFactory factory = RedisConnectionFactory.builder()
            .redisConnectionProperties(redisConnectionProperties).build();
        for(int i=0;i<20;i++){
            RedisClient redisClient = factory.createClient();
            StatefulRedisConnection<String, String> conn = redisClient.connect();
            RedisCommands<String, String> commands = conn.sync();
            String value = commands.get("ly");
            System.out.println(value);
        }
    }

    @org.junit.Test
    public void borrowConnection() {
        RedisConnectionFactory factory = RedisConnectionFactory.builder()
            .redisConnectionProperties(redisConnectionProperties).build();
        for(int i=0;i<20;i++){
            StatefulRedisConnection<String, String> conn = factory.borrowConnection();
            RedisCommands<String, String> commands = conn.sync();
            String value = commands.get("ly");
            System.out.println(value);
            conn.close();
        }
    }
}