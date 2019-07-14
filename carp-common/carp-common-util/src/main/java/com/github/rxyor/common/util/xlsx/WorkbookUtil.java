package com.github.rxyor.common.util.xlsx;

import com.github.rxyor.common.core.exception.CarpIOException;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-14 Sun 13:45:00
 * @since 1.0.0
 */
public class WorkbookUtil {

    private WorkbookUtil() {
    }

    public static XSSFWorkbook createXSSFWorkbook() {
        try {
            return XSSFWorkbookFactory.createWorkbook();
        } catch (Exception e) {
            throw new CarpIOException(e);
        }
    }

    public static XSSFWorkbook createXSSFWorkbook(String path) {
        try {
            return new XSSFWorkbook(path);
        } catch (Exception e) {
            throw new CarpIOException(e);
        }
    }

    public static XSSFWorkbook createXSSFWorkbook(File file) {
        try {
            return new XSSFWorkbook(file);
        } catch (Exception e) {
            throw new CarpIOException(e);
        }
    }

    public static XSSFWorkbook createXSSFWorkbook(InputStream inputStream) {
        try {
            return new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            throw new CarpIOException(e);
        }
    }

    /**
     * 创建一个带有标题列的Excel工作簿
     *
     * @param titles 标题内容
     * @return XSSFWorkbook
     */
    public static XSSFWorkbook createXSSFWorkbookWithTitle(List<String> titles) {
        XSSFWorkbook workbook = createXSSFWorkbook();
        if (titles == null || titles.size() == 0) {
            return workbook;
        }
        XSSFSheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
        }
        return workbook;
    }

    /**
     * 设置列自动宽度
     *
     * @param sheet 表格
     * @param columns 列数
     */
    public static void autoSizeColumn(Sheet sheet, Integer columns) {
        if (sheet == null || columns == null || columns <= 0) {
            return;
        }
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

}
