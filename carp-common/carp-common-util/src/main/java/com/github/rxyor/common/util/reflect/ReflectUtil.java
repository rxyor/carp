package com.github.rxyor.common.util.reflect;

import com.github.rxyor.common.core.exception.NewInstanceException;
import com.github.rxyor.common.core.exception.ReflectException;
import java.lang.reflect.Field;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-05 Wed 14:03:00
 * @since 1.0.0
 */
public class ReflectUtil {

    public static <T> T newInstance(String className) {
        T instance = null;
        try {
            Class clazz = Class.forName(className);
            instance = (T) clazz.newInstance();
        } catch (Exception e) {
            throw new NewInstanceException(e);
        }
        return instance;
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new NewInstanceException(e);
        }
    }

    public static void setFieldValue(Object source, Field field, Object value) {
        if (source == null || field == null || value == null) {
            return;
        }
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(source, value);
        } catch (IllegalAccessException e) {
            throw new ReflectException(e);
        }
    }

    public static Object getFieldValue(Object source, Field field) {
        if (source == null || field == null) {
            return null;
        }
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(source);
        } catch (IllegalAccessException e) {
            throw new ReflectException(e);
        }
    }

}
