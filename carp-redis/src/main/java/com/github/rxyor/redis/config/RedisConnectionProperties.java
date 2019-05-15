package com.github.rxyor.redis.config;

import java.time.Duration;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *<p>
 *Redis Config Properties
 *</p>
 *
 * @author liuyang
 * @date 2019-05-13 Mon 16:10:00
 * @since 1.0.0
 */

@Getter
@Setter
@ToString
public class RedisConnectionProperties {

    /**
     * Default timeout: 60 sec
     */
    public static final long DEFAULT_TIMEOUT = 60;

    /**
     * Redis server host.
     */
    private String host = "localhost";

    /**
     * Redis server port.
     */
    private int port = 6379;

    /**
     * Database index used by the connection factory.
     */
    private int database = 0;

    /**
     * Connection URL. Overrides host, port, and password. User is ignored. Example:
     * redis://user:password@example.com:6379
     */
    private String url;

    /**
     * Login password of the redis server.
     */
    private String password;

    /**
     * Connection timeout.
     */
    private Duration timeout = Duration.ofSeconds(DEFAULT_TIMEOUT);

    /**
     * Connection pool
     */
    private Pool pool;

    /**
     * Pool properties.
     */
    @Data
    public static class Pool {

        /**
         * Maximum number of "idle" connections in the pool. Use a negative value to
         * indicate an unlimited number of idle connections.
         */
        private int maxIdle = 8;

        /**
         * Target for the minimum number of idle connections to maintain in the pool. This
         * setting only has an effect if it is positive.
         */
        private int minIdle = 0;

        /**
         * Maximum number of connections that can be allocated by the pool at a given
         * time. Use a negative value for no limit.
         */
        private int maxActive = 8;

        /**
         * Maximum amount of time a connection allocation should block before throwing an
         * exception when the pool is exhausted. Use a negative value to block
         * indefinitely.
         */
        private Duration maxWait = Duration.ofMillis(-1);
    }

}
