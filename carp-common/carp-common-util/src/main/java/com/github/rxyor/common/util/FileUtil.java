package com.github.rxyor.common.util;

import com.github.rxyor.common.core.exception.ReadFileException;
import java.io.IOException;
import java.nio.charset.Charset;
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
        Optional.ofNullable(clazz).orElseThrow(() -> new IllegalArgumentException("clazz path can't be null"));
        String context;
        try {
            context = IOUtils.toString(clazz.getResourceAsStream(path), Charset.forName("utf-8"));
        } catch (IOException e) {
            throw new ReadFileException();
        }
        return context;
    }

}
