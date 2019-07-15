package com.github.rxyor.common.util.xlsx;

import com.github.rxyor.common.core.exception.CarpIOException;
import com.github.rxyor.common.util.io.IOUtil;
import com.github.rxyor.common.util.io.NIOUtil;
import com.github.rxyor.common.util.reflect.ReflectUtil;
import com.github.rxyor.common.util.string.CharSequenceUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *<p>
 *Excel导入导出工具
 *</p>
 *
 * @author liuyang
 * @date 2019-07-13 Sat 21:02:00
 * @since 1.0.0
 */
public class ExcelHelper<T> {

    /**
     * java 对象类型
     */
    private final Class<T> type;

    /**
     * 导入使用NIO
     */
    private Boolean useNIO = false;

    /**
     * 导入数据源模式
     */
    private Mode mode;

    /**
     * 导入文件路径
     */
    private String path;

    /**
     * 导入文件
     */
    private File file;

    /**
     * 导入输入流
     */
    private InputStream inputStream;

    /**
     * 导出数据集合
     */
    private Collection<T> data;

    /**
     * 导入工作簿
     */
    private Workbook importWorkbook;

    /**
     * 导出工作簿
     */
    private Workbook exportWorkbook;

    private ExcelHelper(Class<T> type) {
        Objects.requireNonNull(type, "type can't be null");
        this.type = type;
    }

    /**
     * 生成一个实例
     *
     * @param type java 对象类型
     * @param <C> 类型
     * @return ExcelHelper
     */
    public static <C> ExcelHelper<C> instance(Class<C> type) {
        return new ExcelHelper<>(type);
    }

    /**
     * 是否使用NIO
     *
     * @param useNIO Boolean
     * @return
     */
    public ExcelHelper<T> useNIO(Boolean useNIO) {
        this.useNIO = (useNIO == null || !useNIO) ? false : true;
        return this;
    }

    /**
     * 导入Excel
     *
     * @return
     */
    public List<T> doImport() {
        this.createImportWorkbook();
        return this.parseExcel(this.importWorkbook);
    }

    /**
     * 设置导入文件路径
     *
     * @param path 文件路径
     * @return
     */
    public ExcelHelper<T> input(String path) {
        this.mode = Mode.PATH;
        this.path = path;
        return this;
    }

    /**
     * 设置导入文件
     *
     * @param file 文件
     * @return
     */
    public ExcelHelper<T> input(File file) {
        this.mode = Mode.FILE;
        this.file = file;
        return this;
    }

    /**
     * 设置导入文件流
     *
     * @param inputStream 输入流
     * @return
     */
    public ExcelHelper<T> input(InputStream inputStream) {
        this.mode = Mode.INPUT_STREAM;
        this.inputStream = inputStream;
        return this;
    }

    /**
     * 创建导入工作簿
     */
    private void createImportWorkbook() {
        Objects.requireNonNull(mode, "you must set input of path or file or inputStream...");
        InputStream is = null;
        try {
            if (useNIO != null && useNIO) {
                switch (mode) {
                    case PATH:
                        is = new ByteArrayInputStream(NIOUtil.readFile(this.path));
                        this.importWorkbook = WorkbookUtil.createXSSFWorkbook(is);
                        break;
                    case FILE:
                        is = new ByteArrayInputStream(NIOUtil.readFile(this.file));
                        this.importWorkbook = WorkbookUtil.createXSSFWorkbook(is);
                        break;
                    default:
                        this.importWorkbook = WorkbookUtil.createXSSFWorkbook(this.inputStream);
                        break;
                }
            } else {
                switch (mode) {
                    case PATH:
                        this.importWorkbook = WorkbookUtil.createXSSFWorkbook(this.path);
                        break;
                    case FILE:
                        this.importWorkbook = WorkbookUtil.createXSSFWorkbook(this.file);
                        break;
                    case INPUT_STREAM:
                        this.importWorkbook = WorkbookUtil.createXSSFWorkbook(this.inputStream);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            throw new CarpIOException(e);
        } finally {
            IOUtil.close(is);
            IOUtil.close(inputStream);
        }
    }

    /**
     * 解析Excel
     *
     * @param workbook 导入工作簿
     * @return 对象List
     */
    private List<T> parseExcel(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        if (workbook == null || sheet == null) {
            throw new CarpIOException("read excel file fail");
        }
        return readData(sheet);
    }

    /**
     * 读取Excel文件的标题行
     *
     * @param sheet 报表
     * @return TitleFieldColumn List
     */
    private List<TitleFieldColumn> readTitle(Sheet sheet) {
        if (sheet == null || sheet.getRow(0) == null) {
            return new ArrayList<>(0);
        }

        Row titleRow = sheet.getRow(0);
        int index = 0;
        List<TitleFieldColumn> titleFieldColumns = new ArrayList<>(32);
        Iterator<Cell> iterator = titleRow.cellIterator();
        while (iterator.hasNext()) {
            titleFieldColumns.add(new TitleFieldColumn(index++,
                CharSequenceUtil.trim(iterator.next().getStringCellValue())));
        }
        return titleFieldColumns;
    }

    /**
     * 读取数据数据行
     *
     * @param sheet 报表
     * @return
     */
    private List<T> readData(Sheet sheet) {
        if (sheet == null) {
            return new ArrayList<>(0);
        }

        List<TitleFieldColumn> titleFieldColumns = this.readTitle(sheet);
        if (titleFieldColumns == null || titleFieldColumns.size() == 0) {
            return new ArrayList<>(0);
        }

        List<T> data = new ArrayList<>(512);
        this.matchAndSetFieldByTitle(titleFieldColumns);

        Iterator<Row> iterator = sheet.rowIterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            //跳过标题列
            if (row.getRowNum() == 0) {
                continue;
            }
            data.add(readRow(row, titleFieldColumns));
        }
        return data;
    }

    /**
     * 读取单行数据
     *
     * @param row 行
     * @param titleFieldColumns
     * @return java 对象
     */
    private T readRow(Row row, List<TitleFieldColumn> titleFieldColumns) {
        if (HashMap.class.equals(type) || AbstractMap.class.equals(type)) {
            return (T) readMapRow(row, titleFieldColumns);
        } else {
            return readPojoRow(row, titleFieldColumns);
        }
    }

    /**
     * 目标对象是pojo时，读取单行数据
     *
     * @param row 行
     * @param titleFieldColumns
     * @return java Pojo对象
     */
    private T readPojoRow(Row row, List<TitleFieldColumn> titleFieldColumns) {
        T instance = ReflectUtil.newInstance(type);
        for (int i = 0; i < titleFieldColumns.size(); i++) {
            TitleFieldColumn column = titleFieldColumns.get(i);
            if (column == null || column.field == null) {
                continue;
            }
            Object value = CellUtil.getCellValue(row.getCell(i), column.field.getType());
            try {
                ReflectUtil.setFieldValue(instance, titleFieldColumns.get(i).field, value);
            } catch (Exception e) {
                //ignore exception
            }
        }
        return instance;
    }

    /**
     * 目标对象是map时，读取单行数据
     *
     * @param row 行
     * @param titleFieldColumns
     * @return hash map
     */
    private Map<String, Object> readMapRow(Row row, List<TitleFieldColumn> titleFieldColumns) {
        Map<String, Object> rowData = new HashMap<>(32);
        for (int i = 0; i < titleFieldColumns.size(); i++) {
            Object value = CellUtil.getCellValue(row.getCell(i), Object.class);
            rowData.put(titleFieldColumns.get(i).getTitle(), value);
        }
        return rowData;
    }

    /**
     *Excel标题与Java 对象的Filed匹配
     *
     * @param titleColumns
     */
    private void matchAndSetFieldByTitle(List<TitleFieldColumn> titleColumns) {
        List<Field> fields = ReflectUtil.getDeclaredFields(type, true);

        Map<String, Field> map = new HashMap<>(fields.size());
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            map.put(column.title(), field);
        }
        titleColumns.forEach(column -> column.setField(map.get(column.title)));
    }

    /**
     * 导出Excel
     *
     * @return
     */
    public byte[] doExport() {
        List<TitleFieldColumn> titleFieldColumns = this.generateTitle();
        this.createExportWorkbook(titleFieldColumns);
        this.writeData(1, titleFieldColumns);
        WorkbookUtil.autoSizeColumn(this.exportWorkbook.getSheetAt(0), titleFieldColumns.size());
        return this.writeExportWorkbook2Byes();
    }

    /**
     * 设置导出的数据
     *
     * @param data 要导出的数据
     * @return
     */
    public ExcelHelper<T> input(Collection<T> data) {
        this.data = data;
        return this;
    }

    /**
     * 创建导出工作簿
     *
     * @param titleFieldColumns
     */
    private void createExportWorkbook(List<TitleFieldColumn> titleFieldColumns) {
        List<String> titles = titleFieldColumns.stream().map(TitleFieldColumn::getTitle).collect(Collectors.toList());
        this.exportWorkbook = WorkbookUtil.createXSSFWorkbookWithTitle(titles);
    }

    /**
     * 写数据到工作簿
     *
     * @param beginRowIndex 开始行的索引
     * @param titleFieldColumns
     */
    private void writeData(int beginRowIndex, List<TitleFieldColumn> titleFieldColumns) {
        if (data == null || data.size() == 0) {
            return;
        }
        int offset = (beginRowIndex < 0) ? 0 : beginRowIndex;
        Sheet sheet = this.exportWorkbook.getSheetAt(0);
        for (T item : data) {
            offset = writeRow(titleFieldColumns, offset, sheet, item);
        }
    }

    /**
     * 写入行到工作簿
     *
     * @param titleFieldColumns
     * @param offset 当前行索引
     * @param sheet 报表
     * @param item 数据项
     * @return 最新行索引
     */
    private int writeRow(List<TitleFieldColumn> titleFieldColumns, int offset, Sheet sheet, T item) {
        if (item == null) {
            return offset;
        }
        Row row = sheet.createRow(offset++);
        for (int i = 0; i < titleFieldColumns.size(); i++) {
            Cell cell = row.createCell(i);
            Field field = Optional.ofNullable(titleFieldColumns.get(i)).map(TitleFieldColumn::getField)
                .orElse(null);
            Object value = null;
            try {
                value = ReflectUtil.getFieldValue(item, field);
            } catch (Exception e) {
                //ignore exception
            }
            CellUtil.setCellValue(cell, value);
        }
        return offset;
    }

    /**
     * 导出Excel为字节数组
     *
     * @return byte[]
     */
    private byte[] writeExportWorkbook2Byes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            this.exportWorkbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new CarpIOException(e);
        } finally {
            IOUtil.close(outputStream);
        }
    }

    /**
     * 读取标题
     *
     * @return
     */
    public List<TitleFieldColumn> generateTitle() {
        List<Field> fields = ReflectUtil.getDeclaredFields(type, true);

        List<TitleFieldColumn> list = new ArrayList<>(fields.size());
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            int index = Optional.ofNullable(column).map(Column::index).orElse(Integer.MAX_VALUE);
            String title = Optional.ofNullable(column).map(Column::title).orElse(null);
            if (title == null || title.trim().length() == 0) {
                continue;
            }
            list.add(new TitleFieldColumn(index, title, field));
        }
        list.sort(Comparator.comparingInt(value -> value.index));
        return list;
    }

    /**
     * 清空配置
     *
     * @return
     */
    public ExcelHelper<T> clear() {
        this.path = null;
        this.file = null;
        this.inputStream = null;
        this.data = null;
        this.importWorkbook = null;
        this.exportWorkbook = null;
        return this;
    }

    /**
     * 包装列对象
     */
    @Data
    public class TitleFieldColumn {

        /**
         * 列下标
         */
        private Integer index;

        /**
         * 列标题
         */
        private String title;

        /**
         * 列对应类字段
         */
        private Field field;

        public TitleFieldColumn() {
        }

        public TitleFieldColumn(Integer index, String title) {
            this.index = index;
            this.title = title;
        }

        public TitleFieldColumn(Integer index, String title, Field field) {
            this.index = index;
            this.title = title;
            this.field = field;
        }
    }

    /**
     * 数据来源模式
     */
    private enum Mode {
        PATH,
        FILE,
        INPUT_STREAM
    }
}
