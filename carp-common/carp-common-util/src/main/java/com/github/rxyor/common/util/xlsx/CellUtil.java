package com.github.rxyor.common.util.xlsx;

import com.github.rxyor.common.util.time.DateUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.NumberToTextConverter;

/**
 *<p>
 *单元格工具类
 *</p>
 *
 * @author liuyang
 * @date 2019-07-14 Sun 00:05:00
 * @since 1.0.0
 */
public class CellUtil {

    private CellUtil() {
    }

    /**
     * 设置单元格的值
     *
     * @param cell 单元格
     * @param value 值
     */
    public static void setCellValue(Cell cell, Object value) {
        if (cell == null || value == null) {
            return;
        }

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Byte) {
            cell.setCellValue((Byte) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(DateUtil.localDateTime2Date((LocalDateTime) value));
        } else if (value instanceof LocalDate) {
            cell.setCellValue(DateUtil.localDate2Date((LocalDate) value));
        } else {
            cell.setCellValue(new HSSFRichTextString(value.toString()));
        }
    }

    /**
     * 获取cell的值
     *
     * @param cell 单元格
     * @param expectedType 期望的类型
     * @return 单元格值
     */
    public static Object getCellValue(Cell cell, Class expectedType) {
        return convertType(getCellValue(cell), expectedType);
    }

    /**
     * 获取cell的值
     *
     * @param cell 单元格
     * @return 单元格值
     */
    @SuppressWarnings("all")
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        CellType type = cell.getCellType();
        switch (type) {
            case _NONE:
                return null;
            case BLANK:
                return null;
            case STRING:
                return getStringCellValue(cell);
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                return cell.getErrorCellValue();
            case NUMERIC:
                return getNumericOrFormulaCellValue(cell);
            case FORMULA:
                return getNumericOrFormulaCellValue(cell);
            default:
                return null;
        }
    }

    private static String getStringCellValue(Cell cell) {
        String value = cell.getStringCellValue();
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value;
    }

    private static Object getNumericOrFormulaCellValue(Cell cell) {
        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        } else {
            return NumberToTextConverter.toText(cell.getNumericCellValue());
        }
    }

    /**
     * 将Object转为期望类型
     *
     * @param value 原值
     * @param expectedType 期望类型
     * @return 期望的值
     */
    private static Object convertType(Object value, Class expectedType) {
        if (value == null) {
            return null;
        }
        if (expectedType == null) {
            return value;
        }

        //如果是期望的类型直接返回
        if (value.getClass().equals(expectedType)) {
            return value;
        }
        if (Byte.class.equals(expectedType)) {
            Double d = Double.parseDouble(value.toString());
            return d != null ? d.byteValue() : null;
        }
        if (Boolean.class.equals(expectedType)) {
            return parseBoolean(value.toString());
        }
        if (Integer.class.equals(expectedType)) {
            Double d = Double.parseDouble(value.toString());
            return d != null ? d.intValue() : null;
        }
        if (Long.class.equals(expectedType)) {
            Double d = Double.parseDouble(value.toString());
            return d != null ? d.longValue() : null;
        }
        if (Float.class.equals(expectedType)) {
            return Float.parseFloat(value.toString());
        }
        if (Double.class.equals(expectedType)) {
            return Double.parseDouble(value.toString());
        }
        if (BigDecimal.class.equals(expectedType)) {
            return new BigDecimal(value.toString());
        }
        if (Date.class.equals(expectedType)) {
            return parseDate(value.toString());
        }
        if (java.sql.Date.class.equals(expectedType)) {
            return new java.sql.Date(parseDate(value.toString()).getTime());
        }

        return value;
    }

    /**
     * 处理Boolean值
     *
     * @param value 值
     * @return
     */
    private static Boolean parseBoolean(String value) {
        if ("true".equals(value)) {
            return true;
        } else if ("false".equals(value)) {
            return false;
        }
        return null;
    }

    /**
     * 处理日期
     *
     * @param value 值
     * @return
     */
    private static Date parseDate(String value) {
        return DateUtil.parse(value);
    }
}
