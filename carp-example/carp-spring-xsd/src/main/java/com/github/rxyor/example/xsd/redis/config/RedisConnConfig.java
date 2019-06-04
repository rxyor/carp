package com.github.rxyor.example.xsd.redis.config;

import lombok.Data;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-03 Mon 14:10:00
 * @since 1.0.0
 */
@Data
public class RedisConnConfig {

    private String host;

    private Integer port;

    private Integer database;

    private String password;

}
