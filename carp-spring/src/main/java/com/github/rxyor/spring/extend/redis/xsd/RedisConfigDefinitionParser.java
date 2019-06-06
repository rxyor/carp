package com.github.rxyor.spring.extend.redis.xsd;

import com.github.rxyor.redis.redisson.config.RedisConfig;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.stereotype.Component;
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
@Slf4j
@Component
public class RedisConfigDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private final static Class<RedisConfig> BEAN_CLASS = RedisConfig.class;

    @Override
    protected Class<?> getBeanClass(Element element) {
        return BEAN_CLASS;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.getRawBeanDefinition().setBeanClass(BEAN_CLASS);
        parseAttribute(element, parserContext, builder);
    }

    private void parseAttribute(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (element == null) {
            return;
        }
        Field[] fields = BEAN_CLASS.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return;
        }
        for (Field field : fields) {
            String propertyName = field.getName();
            String value = element.getAttribute(propertyName);
            if (StringUtils.isNotBlank(value)) {
                builder.addPropertyValue(propertyName, value);
            }
        }
    }
}
