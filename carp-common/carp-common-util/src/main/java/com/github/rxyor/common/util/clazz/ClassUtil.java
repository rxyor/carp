package com.github.rxyor.common.util.clazz;

import com.github.rxyor.common.core.exception.NewInstanceException;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-05 Wed 14:03:00
 * @since 1.0.0
 */
public class ClassUtil {

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

}
