package com.github.rxyor.spring.extend.redis.xsd;

import com.github.rxyor.redis.redisson.config.RedisConfig;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-05 Wed 00:44:00
 * @since 1.0.0
 */
public class AppTest {

    @Test
    public void testRedisDatasourceXsd() {
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
        RedisConfig redisConfig = (RedisConfig) context.getBean("redisConfig");
        System.out.println(redisConfig);
    }
}