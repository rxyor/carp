package com.github.rxyor.common.util.time;

import lombok.Getter;

/**
 *<p>
 *日期格式
 *</p>
 *
 * @author liuyang
 * @date 2019-07-13 Sat 19:34:00
 * @since 1.0.0
 */
public enum DateFormatEnum {
    /**
     * 日期格式枚举
     */
    FORMAT_DEFAULT("yyyy-MM-dd HH:mm:ss"),
    FORMAT_1("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
    FORMAT_2("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
    FORMAT_3("yy-MM-dd HH:mm:ss am"),
    FORMAT_4("EEE MMM dd HH:mm:ss 'GMT' yyyy"),
    FORMAT_5("yyyy/MM/dd HH:mm:ss"),
    FORMAT_6("yyyy/MM/dd'T'HH:mm:ss.SSSZ"),
    FORMAT_7("yyyy/MM/dd'T'HH:mm:ss.SSSXXX"),
    FORMAT_8("yy/MM/dd HH:mm:ss am"),
    FORMAT_9("yy-MM-dd HH:mm"),
    FORMAT_10("yy/MM/dd HH:mm"),
    FORMAT_11("yyMMddHHmmssZ"),
    FORMAT_12("YYYY-'W'ww-u"),
    FORMAT_13("yy-MM-dd"),
    FORMAT_14("yy/MM/dd"),
    FORMAT_15("yy-MM"),
    FORMAT_16("yy/MM"),
    FORMAT_17("yyMM");;

    @Getter
    private String format;

    DateFormatEnum(String format) {
        this.format = format;
    }
}
