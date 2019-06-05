package com.github.rxyor.redis.redisson.config;

import lombok.Data;
import org.redisson.client.codec.Codec;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-04 Tue 10:33:00
 * @since 1.0.0
 */
@Data
public class RedisConfig {

    private String host;

    private Integer port;

    private String password;

    private Integer database;

    private Codec codec;

}
