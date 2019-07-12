package com.github.rxyor.common.util.io;

import java.nio.charset.Charset;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-23 Thu 20:39:00
 * @since 1.0.0
 */
public class IOUtil {

    public static byte[] string2Bytes(String s) {
        if (s == null) {
            return new byte[0];
        }
        return s.getBytes(Charset.forName("utf-8"));
    }

}
