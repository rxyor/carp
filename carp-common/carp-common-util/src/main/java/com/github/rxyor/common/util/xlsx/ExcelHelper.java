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

    private final Class<T> type;

    private Boolean useNIO = false;

    private Mode mode;

    private String path;

    private File file;

    private Collection<T> data;

    private InputStream inputStream;

    private Workbook inputWorkbook;

    private Workbook exportWorkbook;

    private ExcelHelper(Class<T> type) {
        Objects.requireNonNull(type, "type can't be null");
        this.type = type;
    }

    public static <C> ExcelHelper<C> instance(Class<C> type) {
        return new ExcelHelper<>(type);
    }

    public ExcelHelper<T> useNIO(Boolean useNIO) {
        this.useNIO = (useNIO == null || !useNIO) ? false : true;
        return this;
    }

    public List<T> doImport() {
        this.createInputWorkbook();
        return this.parseExcel(this.inputWorkbook);
    }

    public ExcelHelper<T> input(String path) {
        this.mode = Mode.PATH;
        this.path = path;
        return this;
    }

    public ExcelHelper<T> input(File file) {
        this.mode = Mode.FILE;
        this.file = file;
        return this;
    }

    public ExcelHelper<T> input(InputStream inputStream) {
        this.mode = Mode.INPUT_STREAM;
        this.inputStream = inputStream;
        return this;
    }

    private void createInputWorkbook() {
        Objects.requireNonNull(mode, "you must set input of path or file or inputStream...");
        InputStream is = null;
        try {
            if (useNIO != null && useNIO) {
                switch (mode) {
                    case PATH:
                        is = new ByteArrayInputStream(NIOUtil.readFile(this.path));
                        this.inputWorkbook = WorkbookUtil.createXSSFWorkbook(is);
                        break;
                    case FILE:
                        is = new ByteArrayInputStream(NIOUtil.readFile(this.file));
                        this.inputWorkbook = WorkbookUtil.createXSSFWorkbook(is);
                        break;
                    default:
                        this.inputWorkbook = WorkbookUtil.createXSSFWorkbook(this.inputStream);
                        break;
                }
            } else {
                switch (mode) {
                    case PATH:
                        this.inputWorkbook = WorkbookUtil.createXSSFWorkbook(this.path);
                        break;
                    case FILE:
                        this.inputWorkbook = WorkbookUtil.createXSSFWorkbook(this.file);
                        break;
                    case INPUT_STREAM:
                        this.inputWorkbook = WorkbookUtil.createXSSFWorkbook(this.inputStream);
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

    private List<T> parseExcel(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        if (workbook == null || sheet == null) {
            throw new CarpIOException("read excel file fail");
        }
        return readData(sheet);
    }

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

    private T readRow(Row row, List<TitleFieldColumn> titleFieldColumns) {
        if (HashMap.class.equals(type) || AbstractMap.class.equals(type)) {
            return (T) readMapRow(row, titleFieldColumns);
        } else {
            return readPojoRow(row, titleFieldColumns);
        }
    }

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

    private Map<String, Object> readMapRow(Row row, List<TitleFieldColumn> titleFieldColumns) {
        Map<String, Object> rowData = new HashMap<>(32);
        for (int i = 0; i < titleFieldColumns.size(); i++) {
            Object value = CellUtil.getCellValue(row.getCell(i), Object.class);
            rowData.put(titleFieldColumns.get(i).getTitle(), value);
        }
        return rowData;
    }

    private void matchAndSetFieldByTitle(List<TitleFieldColumn> titleColumns) {
        Field[] fields = Optional.ofNullable(type).map(Class::getDeclaredFields)
            .orElse(new Field[0]);

        Map<String, Field> map = new HashMap<>(fields.length);
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            map.put(column.title(), field);
        }
        titleColumns.forEach(column -> column.setField(map.get(column.title)));
    }

    public byte[] doExport() {
        List<TitleFieldColumn> titleFieldColumns = this.generateTitle();
        this.createOutputWorkbook(titleFieldColumns);
        this.writeData(1, titleFieldColumns);
        WorkbookUtil.autoSizeColumn(this.exportWorkbook.getSheetAt(0), titleFieldColumns.size());
        return this.writeExportWorkbook2Byes();
    }

    public ExcelHelper<T> input(Collection<T> data) {
        this.data = data;
        return this;
    }

    private void createOutputWorkbook(List<TitleFieldColumn> titleFieldColumns) {
        List<String> titles = titleFieldColumns.stream().map(TitleFieldColumn::getTitle).collect(Collectors.toList());
        this.exportWorkbook = WorkbookUtil.createXSSFWorkbookWithTitle(titles);
    }

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

    private int writeRow(List<TitleFieldColumn> titleFieldColumns, int offset, Sheet sheet, T item) {
        if (item == null) {
            return offset;
        }
        for (int i = 0; i < titleFieldColumns.size(); i++) {
            Row row = sheet.createRow(offset++);
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

    public List<TitleFieldColumn> generateTitle() {
        Field[] fields = Optional.ofNullable(type).map(Class::getDeclaredFields)
            .orElse(new Field[0]);

        List<TitleFieldColumn> list = new ArrayList<>(fields.length);
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

    private enum Mode {
        PATH,
        FILE,
        INPUT_STREAM
    }
}
