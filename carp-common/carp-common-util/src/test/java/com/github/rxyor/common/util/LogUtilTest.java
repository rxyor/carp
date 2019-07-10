package com.github.rxyor.common.util;

import com.github.rxyor.common.util.log.LogUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-02 Tue 13:52:00
 * @since 1.0.0
 */
public class LogUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtilTest.class);

    @Test
    public void error() {
        LogUtil.error(LOGGER, "code:{} is invalid", "500");
    }
}