package com.github.rxyor.distributed.redisson.delay.spring.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-30 Thu 10:35:00
 * @since 1.0.0
 */
@Data
public class RedisConnConfig {

    public final static String DEFAULT_HOST = "127.0.0.1";
    public final static Integer DEFAULT_PORT = 6379;
    public final static Integer DEFAULT_DATABASE = 0;

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 数据库
     */
    private Integer database;

    /**
     * 密码
     */
    private String password;


    public String address() {
        String host = StringUtils.isEmpty(this.host) ? DEFAULT_HOST : this.host;
        Integer port = this.port == null ? DEFAULT_PORT : this.port;
        return "redis://" + host + ":" + port;
    }

}
