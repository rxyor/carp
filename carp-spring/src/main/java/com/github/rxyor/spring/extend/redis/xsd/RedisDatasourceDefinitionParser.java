package com.github.rxyor.spring.extend.redis.xsd;

import com.github.rxyor.redis.redisson.config.RedisDatasource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-03 Mon 14:53:00
 * @since 1.0.0
 */
public class RedisDatasourceDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return RedisDatasource.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String database = element.getAttribute("database");
        String password = element.getAttribute("password");
        builder.addPropertyValue("host", host);
        builder.addPropertyValue("port", Integer.valueOf(port));
        if (StringUtils.isNotEmpty(password)) {
            builder.addPropertyValue("password", password);
        }
        builder.addPropertyValue("database", Integer.valueOf(database));
    }
}
