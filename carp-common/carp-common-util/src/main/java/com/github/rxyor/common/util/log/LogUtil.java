package com.github.rxyor.common.util.log;

import java.lang.reflect.Constructor;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-02 Tue 13:33:00
 * @since 1.0.0
 */
public class LogUtil {

    private Logger logger;

    private String msg;

    private LogUtil(Logger logger, String msg) {
        Optional.ofNullable(logger)
            .orElseThrow(() -> new IllegalArgumentException("logger can't be null"));
        this.logger = logger;
        this.msg = msg;
    }

    public static LogUtil error(Logger logger, String msg, Object... args) {
        String newMsg = processMsg(msg, args);
        logger.error(newMsg);
        return new LogUtil(logger, newMsg);
    }

    public static LogUtil info(Logger logger, String msg, Object... args) {
        String newMsg = processMsg(msg, args);
        logger.info(newMsg);
        return new LogUtil(logger, newMsg);
    }

    public static LogUtil warn(Logger logger, String msg, Object... args) {
        String newMsg = processMsg(msg, args);
        logger.warn(newMsg);
        return new LogUtil(logger, newMsg);
    }

    public static LogUtil debug(Logger logger, String msg, Object... args) {
        String newMsg = processMsg(msg, args);
        logger.debug(newMsg);
        return new LogUtil(logger, newMsg);
    }

    public RuntimeException andThrow(Class<? extends RuntimeException> type) {
        throw newRuntimeException(this.msg, type);
    }

    private static String processMsg(String format, Object... args) {
        if (args == null || args.length == 0 || StringUtils.isEmpty(format)) {
            return format;
        }
        for (Object arg : args) {
            format = format.replaceFirst("\\{\\}", arg.toString());
        }
        return format;
    }

    private static <T extends RuntimeException> T newRuntimeException(String msg,
        Class<T> exceptionType) {
        Optional.ofNullable(exceptionType)
            .orElseThrow(() -> new IllegalArgumentException("exceptionType can't be null"));
        try {
            Constructor<T> constructor = exceptionType.getDeclaredConstructor(String.class);
            T t = (T) constructor.newInstance(msg);
            return t;
        } catch (Exception e) {
            throw new RuntimeException("反射生成异常实例错误");
        }
    }


}
