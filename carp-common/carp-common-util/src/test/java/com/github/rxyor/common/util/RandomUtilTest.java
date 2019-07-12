package com.github.rxyor.common.util;

import com.github.rxyor.common.util.lang.RandomUtil;
import org.junit.Test;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-04 Tue 16:50:00
 * @since 1.0.0
 */
public class RandomUtilTest {

    @Test
    public void createUuid() {
        String uuid = RandomUtil.createUuid();
        System.out.printf("uuid:" + uuid + ",len:" + uuid.length());
    }

    @Test
    public void shortUuid() {
        String uuid = RandomUtil.shortUuid();
        System.out.printf("uuid:" + uuid + ",len:" + uuid.length());
    }
}