package com.github.rxyor.example.xsd.redis.namespace;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-03 Mon 14:45:00
 * @since 1.0.0
 */
public class RedisNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("redis", new RedisBeanDefinitionParser());
    }
}
