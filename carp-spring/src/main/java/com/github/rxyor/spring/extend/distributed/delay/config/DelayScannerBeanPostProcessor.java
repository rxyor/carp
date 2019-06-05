package com.github.rxyor.spring.extend.distributed.delay.config;

import com.github.rxyor.distributed.redisson.delay.core.ScanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-05 Wed 11:10:00
 * @since 1.0.0
 */
public class DelayScannerBeanPostProcessor implements BeanPostProcessor {

    @Autowired(required = false)
    private ScanWrapper scanWrapper;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (scanWrapper != null) {
            scanWrapper.doScan();
        }
        return null;
    }
}
