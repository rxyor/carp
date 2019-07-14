package com.github.rxyor.common.util.xlsx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019/1/7 Mon 16:04:00
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * 列标题
     */
    String title() default "";

    /**
     * 列下标
     */
    int index() default 0;

}
