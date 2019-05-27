package com.github.rxyor.redis.lettuce.factory;

import com.github.rxyor.redis.lettuce.config.RedisConnectionProperties;
import com.github.rxyor.redis.core.exception.CreateRedisConnectionException;
import com.github.rxyor.redis.core.exception.ReleaseRedisConnectionException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-13 Mon 19:48:00
 * @since 1.0.0
 */
@AllArgsConstructor
public class RedisConnectionFactory {

    private RedisConnectionProperties redisConnectionProperties;

    private GenericObjectPool<StatefulRedisConnection<String, String>> pool;

    private RedisConnectionFactory(RedisConnectionProperties redisConnectionProperties) {
        this.redisConnectionProperties = redisConnectionProperties;
    }

    public static Builder builder() {
        return new Builder();
    }

    public RedisClient createClient() {
        return RedisClient.create(redisURI());
    }

    public StatefulRedisConnection<String, String> borrowConnection() {
        if (pool == null) {
            this.initPool();
        }
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            throw new CreateRedisConnectionException(e);
        }
    }

    public void returnConnection(StatefulRedisConnection connection) {
        try {
            pool.returnObject(connection);
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                return;
            }
            throw new ReleaseRedisConnectionException(e);
        }
    }


    public static class Builder {

        private RedisConnectionProperties redisConnectionProperties;

        public Builder redisConnectionProperties(RedisConnectionProperties redisConnectionProperties) {
            Objects.requireNonNull(redisConnectionProperties, "redisConnectionProperties can't be null");
            this.redisConnectionProperties = redisConnectionProperties;
            return this;
        }

        public Builder pool(RedisConnectionProperties.Pool poolProperties) {
            Objects.requireNonNull(redisConnectionProperties, "pool can't be null");
            this.redisConnectionProperties.setPool(poolProperties);
            return this;
        }

        public RedisConnectionFactory build() {
            return new RedisConnectionFactory(redisConnectionProperties);
        }
    }

    public RedisURI redisURI() {
        RedisURI redisURI = new RedisURI(redisConnectionProperties.getHost(), redisConnectionProperties.getPort(),
            redisConnectionProperties.getTimeout());
        redisURI.setDatabase(redisConnectionProperties.getDatabase());
        if (redisConnectionProperties.getPassword() != null) {
            redisURI.setPassword(redisConnectionProperties.getPassword());
        }
        return redisURI;
    }

    private void initPool() {
        Objects.requireNonNull(redisConnectionProperties.getPool(), "pool can't be null");
        RedisClient redisClient = this.createClient();
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMinIdle(redisConnectionProperties.getPool().getMinIdle());
        poolConfig.setMaxIdle(redisConnectionProperties.getPool().getMaxIdle());
        poolConfig.setMaxTotal(redisConnectionProperties.getPool().getMaxActive());
        poolConfig.setMaxWaitMillis(redisConnectionProperties.getPool().getMaxWait().toMillis());
        pool = ConnectionPoolSupport.createGenericObjectPool(redisClient::connect, poolConfig);
    }
}
