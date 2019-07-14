package com.github.rxyor.common.util.io;

import static org.junit.Assert.*;

import com.github.rxyor.common.util.time.TimeUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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

    @Test
    public void createFileIfNotExist() {
        String path = "/Users/liuyang/Downloads/test/file.txt";
        FileUtil.createFileIfNotExist(path);
    }

    @Test
    public void writeFile() {
        String path = "/Users/liuyang/Downloads/test/file.txt";
        String msg = "I miss you , every minutes, every seconds";
        NIOUtil.writeFile(path, msg.getBytes(Charset.forName("utf-8")));
    }

    @Test
    public void writeFile2() {
        String path = "/Users/liuyang/Downloads/test/file" + TimeUtil.getCurrentSeconds() + ".txt";
        String msg = "I miss you , every minutes, every seconds";
        File file = FileUtil.createFileIfNotExist(path);
        FileOutputStream fos = null;
        FileChannel channel = null;
        ByteBuffer byteBuffer = null;
        try {
            fos = new FileOutputStream(file);
            channel = fos.getChannel();
            byteBuffer = Charset.forName("utf-8").encode(msg);
            channel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            byteBuffer.clear();
            IOUtil.close(channel);
            IOUtil.close(fos);
        }
    }

    @Test
    public void writeFile3() {
        String path = "/Users/liuyang/Downloads/test/file" + TimeUtil.getCurrentSeconds() + ".txt";
        String msg = "I miss you , every minutes, every seconds";

        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = Charset.forName("utf-8").encode(msg);
            NIOUtil.writeFile(path, byteBuffer.array());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            byteBuffer.clear();
        }
    }
}