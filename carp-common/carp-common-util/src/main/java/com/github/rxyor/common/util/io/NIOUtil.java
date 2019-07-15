package com.github.rxyor.common.util.io;

import com.github.rxyor.common.core.exception.CarpIOException;
import com.github.rxyor.common.core.exception.FileNotExistException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *<p>
 *NIO 工具类
 *</p>
 *
 * @author liuyang
 * @date 2019-07-14 Sun 14:10:00
 * @since 1.0.0
 */
public class NIOUtil {

    private NIOUtil() {
    }

    /**
     * NIO read File to byte[]
     *
     * @param path file path
     * @return byte[]
     */
    public static byte[] readFile(String path) {
        if (path == null || path.trim().length() == 0) {
            throw new FileNotExistException("file:" + path + " has error!");
        }
        return readFile(new File(path));
    }

    /**
     * NIO read File to byte[]
     *
     * @param file java io file
     * @return byte[]
     */
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

    /**
     * read NIO channel
     *
     * @param channel NIO channel
     * @return byte[]
     * @throws IOException
     */
    public static byte[] readChannel(FileChannel channel) throws IOException {
        if (channel == null) {
            return null;
        }
        final int capacity = 1024;
        int len = computeChannelByteSize(((Long) channel.size()).intValue(), capacity);
        byte[] dest = new byte[len];
        ByteBuffer buffer = null;
        try {
            buffer = ByteBuffer.allocate(capacity);
            int offset = 0;
            while (channel.read(buffer) != -1) {
                byte[] src = buffer.array();
                System.arraycopy(src, 0, dest, offset, src.length);
                offset += src.length;
                buffer.flip();
            }
        } catch (IOException e) {
            throw new CarpIOException(e);
        } finally {
            if (buffer != null) {
                buffer.clear();
            }
        }
        return dest;
    }

    /**
     * FileChannel read from ByteBuffer
     *
     * @param channel FileChannel
     * @param buffer ByteBuffer
     * @return read offset , -1 is end
     */
    public static int read(FileChannel channel, ByteBuffer buffer) {
        try {
            return channel.read(buffer);
        } catch (IOException e) {
            throw new CarpIOException(e);
        }
    }

    /**
     * compute how match size need to  alloc
     *
     * @param size channel size
     * @param capacity ByteBuffer capacity
     * @return alloc size
     */
    private static int computeChannelByteSize(int size, int capacity) {
        if (size % capacity == 0) {
            return size;
        }
        return (size / capacity + 1) * capacity;
    }

    /**
     * write byte[] to file
     *
     * @param path file path
     * @param src byte[]
     */
    public static void writeFile(String path, byte[] src) {
        File file = FileUtil.createFileIfNotExist(path);
        writeFile(file, src);
    }

    /**
     * write byte[] to file
     *
     * @param file file
     * @param src byte[]
     */
    public static void writeFile(File file, byte[] src) {
        if (file == null || !file.exists() || file.isDirectory()) {
            throw new FileNotExistException("file not exist!");
        }
        FileOutputStream fos = null;
        FileChannel channel = null;
        try {
            fos = new FileOutputStream(file);
            channel = fos.getChannel();
            writeChannel(channel, src);
        } catch (FileNotFoundException e) {
            throw new FileNotExistException(e);
        } catch (Exception e) {
            throw new CarpIOException(e);
        } finally {
            IOUtil.close(channel);
            IOUtil.close(fos);
        }
    }

    /**
     * write byte[] to FileChannel
     *
     * @param channel FileChannel
     * @param src byte[]
     */
    public static void writeChannel(FileChannel channel, byte[] src) {
        if (channel == null || src == null || src.length == 0) {
            return;
        }
        final int capacity = 1024;
        int remainDataLen = src.length;
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        int offset = 0;
        try {
            while (remainDataLen > capacity) {
                for (int i = 0; i < capacity; i++) {
                    buffer.put(src[offset++]);
                }
                buffer.flip();
                channel.write(buffer);
                remainDataLen -= capacity;
                buffer.clear();
            }
            if (remainDataLen != 0) {
                buffer = ByteBuffer.allocate(remainDataLen);
                for (int i = 0; i < remainDataLen; i++) {
                    buffer.put(src[offset++]);
                }
                buffer.flip();
                channel.write(buffer);
            }
        } catch (IOException e) {
            throw new CarpIOException(e);
        } finally {
            if (buffer != null) {
                buffer.clear();
            }
        }
    }

}
