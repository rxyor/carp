package com.github.rxyor.common.util.io;

import com.github.rxyor.common.core.exception.CarpIOException;
import com.github.rxyor.common.core.exception.FileNotExistException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-14 Sun 14:10:00
 * @since 1.0.0
 */
public class NIOUtil {

    private NIOUtil() {
    }

    public static byte[] readFile(String path) {
        if (path == null || path.trim().length() == 0) {
            throw new FileNotExistException("file:" + path + " not exist!");
        }
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            fis = new FileInputStream(path);
            channel = fis.getChannel();
            return readChannel(channel);
        } catch (FileNotExistException e) {
            throw new FileNotExistException(e);
        } catch (IOException e) {
            throw new CarpIOException(e);
        } finally {
            IOUtil.close(channel);
            IOUtil.close(fis);
        }
    }

    public static byte[] readFile(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            throw new FileNotExistException("file not exist!");
        }
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            return readChannel(channel);
        } catch (FileNotExistException e) {
            throw new FileNotExistException(e);
        } catch (IOException e) {
            throw new CarpIOException(e);
        } finally {
            IOUtil.close(channel);
            IOUtil.close(fis);
        }
    }

    public static byte[] readChannel(FileChannel channel) throws IOException {
        if (channel == null) {
            return null;
        }
        final int capacity = 1024;
        int len = computeChannelByteSize(((Long) channel.size()).intValue(), capacity);
        byte[] dest = new byte[len];
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        int offset = 0;
        while (channel.read(buffer) != -1) {
            byte[] src = buffer.array();
            System.arraycopy(src, 0, dest, offset, src.length);
            offset += src.length;
            buffer.flip();
        }
        buffer.clear();
        return dest;
    }

    public static int read(FileChannel channel, ByteBuffer buffer) {
        try {
            return channel.read(buffer);
        } catch (IOException e) {
            throw new CarpIOException(e);
        }
    }

    private static int computeChannelByteSize(int size, int capacity) {
        if (size % capacity == 0) {
            return size;
        }
        return (size / capacity + 1) * capacity;
    }

}
