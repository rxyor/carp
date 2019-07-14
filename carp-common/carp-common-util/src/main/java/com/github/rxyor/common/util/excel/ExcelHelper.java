package com.github.rxyor.common.util.excel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
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
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *<p>
 *解析Excel和导出Excel
 * 1. 只支持简单的Excel报表（第一行是标题行，其余行是数据行）
 * 2. 只支持简单的数据类型（String，Date，Integer，Double）
 *</p>
 *
 * @author liuyang
 * @date 2019/1/8 Tue 13:46:00
 * @since 1.0.0
 */
@Slf4j
public class ExcelHelper<T> {

    private Integer appendRowIndex = 0;
    private List<SortField> sortFieldList;
    private Class<T> beanType;
    private Integer columnNum = 0;

    private ExcelHelper() {
    }

    public static <T> ExcelHelper<T> newInstance(Class<T> beanType) {
        Objects.requireNonNull(beanType, "Bean类型不能为null");
        ExcelHelper helper = new ExcelHelper();
        helper.appendRowIndex = 0;
        helper.beanType = beanType;
        helper.sortFieldList = generateSortFieldList(beanType);
        helper.columnNum = helper.sortFieldList.size();
        return helper;
    }

    /**
     *导出Excel至输入流
     *
     * @author liuyang
     * @date 2019-01-08 Tue 15:30:54
     * @param dataItems 数据集
     * @return InputStream
     */
    public InputStream exportExcel(Collection<T> dataItems) {
        if (dataItems == null) {
            throw new RuntimeException("导出至Excel数据集不能为null");
        }

        ByteArrayInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            exportExcel(dataItems, outputStream);
            byte[] b = outputStream.toByteArray();
            inputStream = new ByteArrayInputStream(b);
            outputStream.close();
        } catch (Exception e) {
            log.error("导出Excel失败, error:{}", e);
        } finally {
            close(outputStream);
        }
        return inputStream;
    }

    /**
     *导出Excel
     *
     * @author liuyang
     * @date 2019-01-08 Tue 15:30:54
     * @param dataItems 数据集
     * @param outputStream 输入流
     */
    public void exportExcel(Collection<T> dataItems, OutputStream outputStream) {
        if (dataItems == null) {
            throw new RuntimeException("导出至Excel数据集不能为null");
        }
        if (outputStream == null) {
            throw new RuntimeException("Excel输出流不能为null");
        }
        //还原标志位
        initExportConfig();

        XSSFWorkbook workbook = createXSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        if (workbook == null || sheet == null) {
            throw new RuntimeException("导出Excel失败, 无法创建工作簿");
        }
        appendTitle(sheet, appendRowIndex++);
        for (T item : dataItems) {
            appendRow(sheet, item, appendRowIndex++);
        }
        autoSizeColumn(sheet);
        writeToOutputStream(workbook, outputStream);
    }

    /**
     *解析Excel为java bean
     *
     * @author liuyang
     * @date 2019-01-08 Tue 15:32:07
     * @param inputStream excel文件输入流
     * @return List
     */
    public List<T> parseExcel(InputStream inputStream) {
        Workbook workbook = createXSSFWorkbook(inputStream);
        Sheet sheet = null;
        if (workbook != null) {
            sheet = workbook.getSheetAt(0);
        }
        if (workbook == null || sheet == null) {
            throw new RuntimeException("读取Excel文件失败");
        }
        return readData(sheet);
    }

    private void initExportConfig() {
        this.appendRowIndex = 0;
    }

    private Map<String, Integer> readTitle(Sheet sheet) {
        if (sheet == null) {
            return new HashMap<>(0);
        }
        Map<String, Integer> titleColumnMap = new HashMap<>(32);
        Row titleRow = sheet.getRow(0);
        //标题列
        Iterator<Cell> cellIterator = titleRow.cellIterator();
        int index = 0;
        while (cellIterator.hasNext()) {
            String value = cellIterator.next().getStringCellValue();
            titleColumnMap.put(value, index);
            index++;
        }
        return titleColumnMap;
    }

    @SuppressWarnings("all")
    private List<T> readData(Sheet sheet) {
        if (sheet == null) {
            return new ArrayList<>(0);
        }

        List<T> dataList = new ArrayList<>(64);
        Map<String, Integer> titleColumnMap = readTitle(sheet);

        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            //跳过标题列
            if (row.getRowNum() == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = row.cellIterator();
            //读取该行每个单元格的值
            Map<Integer, Object> valueMap = new HashMap<>(32);
            for (int i = 0; i < titleColumnMap.size(); i++) {
                Object value = getCellValue(row.getCell(i));
                valueMap.put(i, value);
            }

            if (beanType == HashMap.class || beanType.getSuperclass() == AbstractMap.class) {
                Map<String, Object> columnData = new HashMap<String, Object>(64);
                for (Map.Entry<String, Integer> entry : titleColumnMap.entrySet()) {
                    Object beanValue = valueMap.get(entry.getValue());
                    columnData.put(entry.getKey(), beanValue);
                }
                dataList.add((T) columnData);
            } else {
                try {
                    T columnData = beanType.newInstance();
                    for (SortField sortField : this.sortFieldList) {
                        Field field = sortField.getField();
                        if (field == null) {
                            continue;
                        }
                        ExcelColumn excelCell = field.getAnnotation(ExcelColumn.class);
                        if (excelCell == null) {
                            continue;
                        }
                        String columnTitle = Optional.ofNullable(excelCell.title()).orElse(field.getName());
                        Integer index = titleColumnMap.get(columnTitle);
                        if (index != null && valueMap.get(index) != null) {
                            injectField(field, columnData, valueMap.get(index));
                        }
                    }
                    dataList.add(columnData);
                } catch (Exception e) {
                    log.error("创建对象失败: error:{}", e);
                    throw new RuntimeException("创建对象失败: " + e.getMessage());
                }
            }
        }
        return dataList;
    }


    @SuppressWarnings("all")
    private XSSFWorkbook createXSSFWorkbook() {
        XSSFWorkbook xssfWorkbook = null;
        try {
            xssfWorkbook = (XSSFWorkbook) WorkbookFactory.create(true);
        } catch (IOException e) {
            log.error("创建Excel工作簿异常: error:{}", e);
        }
        return xssfWorkbook;
    }

    @SuppressWarnings("all")
    private XSSFWorkbook createXSSFWorkbook(InputStream inputStream) {
        XSSFWorkbook xssfWorkbook = null;
        try {
            xssfWorkbook = new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            log.error("读取Excel文件失败: error:{}", e);
        }
        return xssfWorkbook;
    }

    private void appendTitle(Sheet sheet, int rowIndex) {
        if (sheet == null) {
            return;
        }
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < sortFieldList.size(); i++) {
            Field field = sortFieldList.get(i).getField();
            ExcelColumn excelCell = field.getAnnotation(ExcelColumn.class);
            if (excelCell != null) {
                Cell cell = row.createCell(i);
                String title = excelCell.title();
                if (StringUtils.isBlank(title)) {
                    title = field.getName();
                }
                setCellValue(cell, title);
            }
        }
    }

    private void appendRow(Sheet sheet, Object bean, int rowIndex) {
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

    /**
     * 自动行宽
     * @param sheet sheet
     */
    private void autoSizeColumn(Sheet sheet) {
        if (sheet != null) {
            for (int i = 0; i < this.columnNum; i++) {
                sheet.autoSizeColumn(i);
            }
        }
    }

    /**
     * Excel工作簿输出至输出流
     *
     * @param workbook Excel工作簿
     * @param outputStream 输出流
     */
    private void writeToOutputStream(Workbook workbook, OutputStream outputStream) {
        if (workbook == null || outputStream == null) {
            return;
        }
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            log.error("数据写入输出流失败, error{}", e);
        }
    }

    /**
     * 获取类中包含ExcelColumn注解的Field列表
     *
     * @param clazz 类类型
     * @return List
     */
    @SuppressWarnings("all")
    private static <T> List<SortField> generateSortFieldList(Class<T> clazz) {
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
        // 根据字段上的ExcelColumn注解的index值排序
        Collections.sort(sortFieldList);
        return sortFieldList;
    }

    @SuppressWarnings("all")
    private void injectField(Field field, Object target, Object value) {
        try {
            Object convertValue = convertType(field.getType(), value.toString());
            value = (convertValue == null ? value : convertValue);
            if (value != null) {
                field.setAccessible(true);
                field.set(target, value);
            }
        } catch (Exception e) {
            log.error("注入{}字段失败，error: {}", field.getName(), e);
        }
    }

    private Object getFieldValue(Object bean, Field field) {
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

    @SuppressWarnings("all")
    private Object convertType(Class expectedType, Object value) {
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
        } else if (java.sql.Date.class.equals(expectedType)) {
            return new java.sql.Date(parseDate(value.toString()).getTime());
        }

        return value;
    }

    private Boolean parseBoolean(String value) {
        if ("true".equals(value)) {
            return true;
        } else if ("false".equals(value)) {
            return false;
        }
        return null;
    }

    private Date parseDate(String dateStr) {
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

    private void close(Closeable x) {
        if (x != null) {
            try {
                x.close();
            } catch (IOException e) {
                log.error("关闭流或文件失败, error{}", e);
            }
        }
    }


    @SuppressWarnings("all")
    private Object getCellValue(Cell cell) {
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
                        value = NumberToTextConverter.toText(cell.getNumericCellValue());
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
                    value = NumberToTextConverter.toText(cell.getNumericCellValue());
                }
                break;
            }
            default:
                break;
        }
        return value;
    }

    private void setCellValue(Cell cell, Object value) {
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
            for (ExcelUtil.DateFormat format : ExcelUtil.DateFormat.values()) {
                list.add(format.getValue());
            }
            return list;
        }
    }
}
