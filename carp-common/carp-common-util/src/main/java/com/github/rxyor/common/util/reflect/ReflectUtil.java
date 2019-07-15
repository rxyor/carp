package com.github.rxyor.common.util.reflect;

import com.github.rxyor.common.core.exception.NewInstanceException;
import com.github.rxyor.common.core.exception.ReflectException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    /**
     *获取类的Declared Field,
     *
     * @author liuyang
     * @date 2019-07-15 Mon 11:19:47
     * @param c 类
     * @param containSuperClass 是否包含父类
     * @return Filed List
     */
    public static List<Field> getDeclaredFields(Class c, boolean containSuperClass) {
        if (c == null || Object.class.equals(c)) {
            return new ArrayList<>(0);
        }

        List<Field> fieldList = new ArrayList<>(64);

        if (!containSuperClass) {
            Field[] fields = c.getDeclaredFields();
            if (fields == null || fields.length == 0) {
                return fieldList;
            }
            for (Field field : fields) {
                fieldList.add(field);
            }
            return fieldList;
        }

        Class parent = c;
        while (!(parent.getSuperclass() == null) && !Object.class.equals(parent)) {
            Field[] fields = parent.getDeclaredFields();
            parent = parent.getSuperclass();
            if (fields == null && fields.length == 0) {
                continue;
            }
            for (Field field : fields) {
                fieldList.add(field);
            }
        }
        return fieldList;
    }

}
