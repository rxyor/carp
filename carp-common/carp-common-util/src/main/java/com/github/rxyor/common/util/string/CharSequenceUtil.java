package com.github.rxyor.common.util.string;

import java.nio.charset.Charset;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-23 Thu 20:55:00
 * @since 1.0.0
 */
public class CharSequenceUtil {

    public static char[] string2CharArray(String s) {
        return s == null ? new char[0] : s.toCharArray();
    }

    public static String bytes2String(byte[] bytes) {
        return (bytes == null || bytes.length == 0) ? null : new String(bytes, Charset.forName("utf-8"));
    }

    public static String trim(String s) {
        if (s != null) {
            return s.trim();
        }
        return s;
    }
}
