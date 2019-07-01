package com.github.rxyor.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-01 Mon 17:53:00
 * @since 1.0.0
 */
@Slf4j
public class Exceptional<T extends RuntimeException> {

    private static final Exceptional<? extends Exception> EMPTY = new Exceptional<>();

    private final Logger logger;

    private T exception;

    private Exceptional() {
        this.logger = null;
    }

    private Exceptional(Logger logger) {
        Objects.requireNonNull(logger, "logger can't be null");
        this.logger = logger;
    }

    public static <T> Exceptional<? extends Exception> on(Logger logger) {
        return EMPTY;
    }

    public Exceptional<T> exception(T e) {
        this.exception = e;
        return this;
    }

    public Exceptional<T> msg(String format, Object... args) {
        String msg = String.format(format, args);
        this.exception = newT(msg);
        return this;
    }

    public void error() {
        if (this.exception == null) {
            throw newT();
        } else {
            logger.error(this.exception.getMessage());
            throw this.exception;
        }
    }

    public void info() {
        if (this.exception == null) {
            throw newT();
        } else {
            logger.info(this.exception.getMessage());
            throw this.exception;
        }
    }

    public void warn() {
        if (this.exception == null) {
            throw newT();
        } else {
            logger.warn(this.exception.getMessage());
            throw this.exception;
        }
    }

    private T newT(String msg) {
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass()
            .getGenericSuperclass();
        Class<T> classType = (Class<T>) parameterizedType.getActualTypeArguments()[0];
        T t = null;
        try {
            Constructor<T> constructor = classType.getDeclaredConstructor(String.class);
            t = (T) constructor.newInstance(msg);
        } catch (Exception e) {
            log.error("new instance by reflect error:{}", e.getMessage());
        }
        return t;
    }

    private T newT() {
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass()
            .getGenericSuperclass();
        Class<T> classType = (Class<T>) parameterizedType.getActualTypeArguments()[0];
        T t = null;
        try {
            t = (T) classType.newInstance();
        } catch (Exception e) {
            log.error("new instance by reflect error:{}", e.getMessage());
        }
        return t;
    }

}
