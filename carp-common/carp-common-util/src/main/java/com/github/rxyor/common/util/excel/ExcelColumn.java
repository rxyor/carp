package com.github.rxyor.common.util.excel;

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
public @interface ExcelColumn {

    /**
     * Excel列标题
     */
    String title() default "";

    /**
     * Excel第几列(从0开始)，用于导出Excel
     */
    int index() default Integer.MAX_VALUE;

}
