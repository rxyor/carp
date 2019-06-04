package com.github.rxyor.example.xsd.redis;

import com.github.rxyor.example.xsd.redis.config.RedisConnConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-03 Mon 17:49:00
 * @since 1.0.0
 */
public class App {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
        RedisConnConfig redisConnConfig = (RedisConnConfig) context.getBean("redisConnConfig");
        System.out.println(redisConnConfig);
    }


}
