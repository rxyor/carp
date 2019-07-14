package com.github.rxyor.common.util.io;

import java.nio.charset.Charset;
import org.junit.Test;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-14 Sun 18:15:00
 * @since 1.0.0
 */
public class NIOUtilTest {

    private String msg = "I miss you , every minutes, every seconds";

    @Test
    public void writeFile() {
        String path = "/tmp/nio.txt";
        String msg = "Hello, NIO";
        NIOUtil.writeFile(path, msg.getBytes(Charset.forName("utf-8")));
    }

    @Test
    public void readFile() {
        String path = "/tmp/nio.txt";
        byte[] bytes = NIOUtil.readFile(path);
        String s = new String(bytes, Charset.forName("utf-8"));
        System.out.println(s);
    }

}