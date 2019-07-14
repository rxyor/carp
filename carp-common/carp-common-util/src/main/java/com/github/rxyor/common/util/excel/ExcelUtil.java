package com.github.rxyor.common.util.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019/1/7 Mon 15:58:00
 * @since 1.0.0
 */
@Slf4j
public class ExcelUtil {

    public static <T> void exportExcel(Class<T> clazz, Collection<T> data, OutputStream outputStream) {
        if (clazz == null) {
            throw new RuntimeException("元素类型不能为null");
        }
        if (data == null) {
            throw new RuntimeException("导出至Excel数据集不能为null");
        }
        if (outputStream == null) {
            throw new RuntimeException("Excel输出流不能为null");
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        List<SortField> sortFieldList = getSortFieldList(clazz);
        appendTitle(sheet, clazz, sortFieldList, 0);
        int i = 0;
        for (T t : data) {
            appendRow(sheet, t, sortFieldList, ++i);
        }
        try {
            int columns = sortFieldList.size();
            for(int c=0;c<columns;c++){
                sheet.autoSizeColumn(c);
            }
            workbook.write(outputStream);
        } catch (IOException e) {
            log.error("数据写入Excel失败, error{}", e);
        }
    }

    private static <T> void appendTitle(Sheet sheet, Class<T> clazz, List<SortField> sortFieldList, int rowIndex) {
        if (sheet == null || clazz == null) {
            return;
        }

        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < sortFieldList.size(); i++) {
            Field field = sortFieldList.get(i).getField();
            ExcelColumn excelCell = field.getAnnotation(ExcelColumn.class);
            if (excelCell != null) {
                String title = excelCell.title();
                if (StringUtils.isBlank(title)) {
                    title = field.getName();
                }
                Cell cell = row.createCell(i);
                setCellValue(cell, title);
            }
        }
    }

    private static <T> void appendRow(Sheet sheet, Object bean, List<SortField> sortFieldList, int rowIndex) {
        if (sheet == null || bean == null) {
            return;
        }
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < sortFieldList.size(); i++) {
            Field field = sortFieldList.get(i).getField();
            Object value = getFieldValue(bean, field);
            if (value != null) {
                Cell cell = row.createCell(i);
                setCellValue(cell, value);
            }
        }
    }

    private static <T> List<SortField> getSortFieldList(Class<T> clazz) {
        if (clazz == null) {
            return new ArrayList<>(0);
        }
        List<SortField> sortFieldList = new ArrayList<>(32);
        Field[] fields = Optional.ofNullable(clazz.getDeclaredFields()).orElse(new Field[0]);
        for (Field field : fields) {
            ExcelColumn excelCell = field.getAnnotation(ExcelColumn.class);
            if (excelCell == null) {
                continue;
            }
            Integer index = Optional.ofNullable(excelCell.index()).orElse(Integer.MAX_VALUE);
            SortField sortFiled = new SortField(field, index);
            sortFieldList.add(sortFiled);
        }
        // 根据字段上的注解排序
        Collections.sort(sortFieldList);

        return sortFieldList;
    }

    private static Object getFieldValue(Object bean, Field field) {
        if (bean == null || field == null) {
            return null;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        Object value = null;
        try {
            value = field.get(bean);
        } catch (IllegalAccessException e) {
            log.error("从字段{}获取值失败, error{}", field.getName(), e);
        }
        return value;
    }

    private static void setCellValue(Cell cell, Object value) {
        if (cell == null || value == null) {
            return;
        }

        if (value instanceof String) {
            String val = (String) value;
            cell.setCellValue(val);
        } else if (value instanceof Integer) {
            Integer val = (Integer) value;
            cell.setCellValue(val);
        } else if (value instanceof Float) {
            Float val = (Float) value;
            cell.setCellValue(val);
        } else if (value instanceof Double) {
            Double val = (Double) value;
            cell.setCellValue(val);
        } else if (value instanceof Long) {
            Long val = (Long) value;
            cell.setCellValue(val);
        } else if (value instanceof Boolean) {
            Boolean val = (Boolean) value;
            cell.setCellValue(val);
        } else if (value instanceof Byte) {
            Byte val = (Byte) value;
            cell.setCellValue(val);
        } else if (value instanceof Date) {
            Date val = (Date) value;
            SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DEFAULT.getValue());
            cell.setCellValue(sdf.format(val));
        } else if (value instanceof BigDecimal) {
            BigDecimal val = (BigDecimal) value;
            cell.setCellValue(val.doubleValue());
        } else {
            RichTextString val = new HSSFRichTextString(value.toString());
            cell.setCellValue(val);
        }
    }

    /**
     *将Excel转化为Bean
     *
     * @author liuyang
     * @date 2019-01-08 Tue 10:01:41
     * @param clazz Bean类类型
     * @param inputStream Excel文件输入流
     * @return List
     */
    @SuppressWarnings("all")
    public static <T> List<T> parseExcel(Class<T> clazz, InputStream inputStream) {
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
            sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            log.error("读取Excel文件失败: error:{}", e);
        }
        if (workbook == null || sheet == null) {
            return new ArrayList<>(0);
        }

        List<T> data = new ArrayList<>(64);
        Map<String, Integer> titleColumn = new HashMap<>();

        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            //标题列
            if (row.getRowNum() == 0) {
                Iterator<Cell> cellIterator = row.cellIterator();
                int index = 0;
                while (cellIterator.hasNext()) {
                    String value = cellIterator.next().getStringCellValue();
                    titleColumn.put(value, index);
                    index++;
                }
                continue;
            }

            Iterator<Cell> cellIterator = row.cellIterator();
            List<Object> valueList = new ArrayList<>(32);
            while (cellIterator.hasNext()) {
                Object value = getCellValue(cellIterator.next());
                valueList.add(value);
            }

            if (clazz == HashMap.class || clazz.getSuperclass() == AbstractMap.class) {
                Map<String, Object> columnData = new HashMap<String, Object>(64);
                for (Map.Entry<String, Integer> entry : titleColumn.entrySet()) {
                    Object beanValue = valueList.get(entry.getValue());
                    columnData.put(entry.getKey(), beanValue);
                }
                data.add((T) columnData);
            } else {
                try {
                    T columnData = clazz.newInstance();
                    Field[] fields = Optional.ofNullable(clazz.getDeclaredFields()).orElse(new Field[0]);
                    for (Field field : fields) {
                        ExcelColumn excelCell = field.getAnnotation(ExcelColumn.class);
                        if (excelCell == null) {
                            continue;
                        }
                        String columnTitle = Optional.ofNullable(excelCell.title()).orElse(field.getName());
                        Integer index = titleColumn.get(columnTitle);
                        if (index != null && valueList.get(index) != null) {
                            injectField(field, columnData, valueList.get(index));
                        }
                    }
                    data.add(columnData);
                } catch (Exception e) {
                    log.error("创建对象失败: error:{}", e);
                    throw new RuntimeException("创建对象失败: " + e.getMessage());
                }
            }
        }

        return data;
    }

    @SuppressWarnings("all")
    private static Object getCellValue(Cell cell) {
        Object value = null;
        if (cell == null) {
            return value;
        }

        CellType cellType = cell.getCellType();
        switch (cell.getCellType()) {
            case BLANK: {
                value = null;
                break;
            }
            case STRING: {
                value = cell.getStringCellValue();
                if (StringUtils.isBlank((CharSequence) value)) {
                    value = null;
                }
                break;
            }
            case BOOLEAN: {
                value = cell.getBooleanCellValue();
                break;
            }
            case ERROR: {
                value = cell.getErrorCellValue();
                break;
            }
            case FORMULA: {
                try {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        value = cell.getDateCellValue();
                    } else {
                        value = cell.getNumericCellValue();
                    }
                } catch (IllegalStateException e) {
                    value = cell.getRichStringCellValue();
                }
                break;
            }
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                } else {
                    value = cell.getNumericCellValue();
                }
                break;
            }
            default:
                break;
        }

        return value;
    }

    @SuppressWarnings("all")
    private static void injectField(Field field, Object target, Object value) {
        try {
            Object convertValue = convertType(field.getType(), value.toString());
            value = (convertValue == null ? value : convertValue);
            if (value != null) {
                field.setAccessible(true);
                field.set(target, value);
            }
        } catch (Exception e) {
            log.error("注入Field失败，error: {}", e);
        }
    }

    @SuppressWarnings("all")
    private static Object convertType(Class expectedType, Object value) {
        if (expectedType == null || value == null) {
            return null;
        }

        if (value.getClass().equals(expectedType)) {
            return value;
        }

        if (Integer.class.equals(expectedType)) {
            Double d = Double.parseDouble(value.toString());
            return d != null ? d.intValue() : null;
        } else if (Long.class.equals(expectedType)) {
            Double d = Double.parseDouble(value.toString());
            return d != null ? d.longValue() : null;
        } else if (Double.class.equals(expectedType)) {
            return Double.parseDouble(value.toString());
        } else if (Boolean.class.equals(expectedType)) {
            return parseBoolean(value.toString());
        } else if (Byte.class.equals(expectedType)) {
            Double d = Double.parseDouble(value.toString());
            return d != null ? d.byteValue() : null;
        } else if (BigDecimal.class.equals(expectedType)) {
            return new BigDecimal(value.toString());
        } else if (Date.class.equals(expectedType)) {
            return parseDate(value.toString());
        }

        return null;
    }

    private static Boolean parseBoolean(String value) {
        if ("true".equals(value)) {
            return true;
        } else if ("false".equals(value)) {
            return false;
        }
        return null;
    }

    private static Date parseDate(String dateStr) {
        Date date = null;
        if (StringUtils.isEmpty(dateStr)) {
            return date;
        }
        try {
            date = DateUtils.parseDate(dateStr, DateFormat.commonForamtInArray());
        } catch (Exception e) {
            log.error("String转换为Date类型时出现异常, error:{}", e);
        }
        return date;
    }

    /**
     * 日期格式
     */
    @SuppressWarnings("all")
    public enum DateFormat {
        DEFAULT("yyyy-MM-dd HH:mm:ss"),
        DF1("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
        DF2("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
        DF3("yy-MM-dd HH:mm:ss am"),
        DF4("EEE MMM dd HH:mm:ss 'GMT' yyyy"),
        DF5("yyyy/MM/dd HH:mm:ss"),
        DF6("yyyy/MM/dd'T'HH:mm:ss.SSSZ"),
        DF7("yyyy/MM/dd'T'HH:mm:ss.SSSXXX"),
        DF8("yy/MM/dd HH:mm:ss am"),
        DF9("yy-MM-dd HH:mm"),
        DF10("yy/MM/dd HH:mm"),
        DF11("yyMMddHHmmssZ"),
        DF12("YYYY-'W'ww-u"),
        DF13("yy-MM-dd"),
        DF14("yy/MM/dd"),
        DF15("yy-MM"),
        DF16("yy/MM"),
        DF17("yyMM");

        private String value;

        DateFormat(String format) {
            this.value = format;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static String[] commonForamtInArray() {
            List<String> list = commonFormatInList();
            return list.toArray(new String[list.size()]);
        }

        public static List<String> commonFormatInList() {
            List<String> list = new ArrayList<>(32);
            for (DateFormat format : DateFormat.values()) {
                list.add(format.getValue());
            }
            return list;
        }
    }
}
