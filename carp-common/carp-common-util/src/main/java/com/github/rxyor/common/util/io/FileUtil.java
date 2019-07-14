package com.github.rxyor.common.util.io;

import com.github.rxyor.common.core.exception.CarpIOException;
import com.github.rxyor.common.core.exception.FileNotExistException;
import com.github.rxyor.common.core.exception.ReadFileException;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.io.IOUtils;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-15 Wed 15:14:00
 * @since 1.0.0
 */
public class FileUtil {

    public static String readTextFromResource(Class clazz, String path) {
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("file path can't be null"));
        Optional.ofNullable(clazz).orElseThrow(() -> new IllegalArgumentException("reflect can't be null"));
        String context;
        try {
            context = IOUtils.toString(clazz.getResourceAsStream(path), Charset.forName("utf-8"));
        } catch (IOException e) {
            throw new ReadFileException();
        }
        return context;
    }

    public static String readText(String path) {
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("file path can't be null"));
        String context;
        InputStream is = null;
        try {
            is = readInputStream(path);
            context = IOUtils.toString(is, Charset.forName("utf-8"));
        } catch (IOException e) {
            throw new ReadFileException();
        } finally {
            close(is);
        }
        return context;
    }

    public static File readFile(String path) {
        Objects.requireNonNull(path, "path can't be null");
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotExistException(path + " not exists");
        }
        return file;
    }

    public static InputStream readInputStream(String path) {
        InputStream is = null;
        try {
            is = new FileInputStream(readFile(path));
        } catch (Exception e) {
            throw new CarpIOException(e);
        }
        return is;
    }

    public static String findRealPathByClasspath(Class clazz, String path) {
        Objects.requireNonNull(clazz, "reflect  can't be null");
        Objects.requireNonNull(path, "file path can't be null");
        return clazz.getResource(path).getFile();
    }

    public static void close(Closeable x) {
        if (x != null) {
            try {
                x.close();
            } catch (IOException e) {
                throw new CarpIOException(e);
            }
        }
    }


}
