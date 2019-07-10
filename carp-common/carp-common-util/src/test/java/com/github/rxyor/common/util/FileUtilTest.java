package com.github.rxyor.common.util;


import com.github.rxyor.common.util.io.FileUtil;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-04 Tue 12:45:00
 * @since 1.0.0
 */
public class FileUtilTest {

    @org.junit.Test
    public void findRealPathByClasspath() {
        String actual = FileUtil.findRealPathByClasspath(FileUtilTest.class, "/redis.yml");
        System.out.println(actual);
    }
}