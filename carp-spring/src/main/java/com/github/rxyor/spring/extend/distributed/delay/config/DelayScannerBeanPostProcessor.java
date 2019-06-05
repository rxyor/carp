package com.github.rxyor.spring.extend.distributed.delay.config;

import com.github.rxyor.distributed.redisson.delay.core.ScanWrapper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-05 Wed 11:10:00
 * @since 1.0.0
 */
public class DelayScannerBeanPostProcessor implements InitializingBean, DisposableBean {

    @Autowired(required = false)
    private ScanWrapper scanWrapper;

    @Override
    public void destroy() throws Exception {
        if (scanWrapper != null && scanWrapper.getScanner() != null) {
            scanWrapper.getScanner().shutDown();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (scanWrapper != null) {
            scanWrapper.doScan();
        }
    }
}
