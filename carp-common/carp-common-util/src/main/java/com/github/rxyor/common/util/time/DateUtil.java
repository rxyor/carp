package com.github.rxyor.common.util.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.time.DateUtils;

/**
 *<p>
 *日期工具类
 *</p>
 *
 * @author liuyang
 * @date 2019-07-13 Sat 19:33:00
 * @since 1.0.0
 */
public class DateUtil {

    private DateUtil() {
    }

    /**
     * LocalDateTime to LocalDate
     *
     * @param localDateTime LocalDateTime
     * @return LocalDate
     */
    public static LocalDate localDateTime2LocalDate(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "LocalDateTime can't be null");
        return localDateTime.toLocalDate();
    }

    /**
     * LocalDate 2 LocalDateTime
     *
     * @param localDate LocalDate
     * @return LocalDateTime
     */
    public static LocalDateTime localDate2LocalDateTime(LocalDate localDate) {
        Objects.requireNonNull(localDate, "LocalDate can't be null");
        return localDate.atStartOfDay();
    }

    /**
     * LocalDate to Date
     *
     * @param localDate LocalDateTime
     * @return Date
     */
    public static Date localDate2Date(LocalDate localDate) {
        Objects.requireNonNull(localDate, "LocalDate can't be null");
        return localDateTime2Date(localDate.atStartOfDay());
    }

    /**
     * LocalDateTime to Date
     *
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "LocalDateTime can't be null");
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * Date to LocalDate
     *
     * @param date Date
     * @return LocalDate
     */
    public static LocalDate date2LocalDate(Date date) {
        Objects.requireNonNull(date, "Date can't be null");
        LocalDateTime ldt = date2LocalDateTime(date);
        return ldt.toLocalDate();
    }

    /**
     * Date to LocalDateTime
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        Objects.requireNonNull(date, "Date can't be null");
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * date string to date
     *
     * @param str Date String
     * @return Date
     */
    public static Date parse(String str) {
        DateFormatEnum[] formatEnums = DateFormatEnum.values();
        String[] formats = new String[formatEnums.length];
        for (int i = 0; i < formatEnums.length; i++) {
            formats[i] = formatEnums[i].getFormat();
        }
        try {
            return DateUtils.parseDate(str, formats);
        } catch (ParseException e) {
            throw new IllegalArgumentException(str + " can't be converted to date, date format is not supported");
        }
    }

    /**
     * date string to date by format
     *
     * @param str Date String
     * @param format Date Format
     * @return Date
     */
    public static Date parse(String str, String format) {
        if (str == null || str.trim().length() == 0
            || format == null || format.length() == 0) {
            throw new IllegalArgumentException(str + " can't be converted to date which use format: " + format);
        }
        LocalDateTime ldt = LocalDateTime.parse(str, DateTimeFormatter.ofPattern(format));
        return localDateTime2Date(ldt);
    }

    /**
     * 日期转String格式
     *
     * @param date 日期
     * @param format 格式
     * @return String格式的日期
     */
    public static String date2String(Date date, String format) {
        if (date == null) {
            return null;
        }
        if (format == null || format.trim().trim().length() == 0) {
            throw new IllegalArgumentException("Not supported date format: " + format);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Not supported date format: " + format);
        }
    }
}
