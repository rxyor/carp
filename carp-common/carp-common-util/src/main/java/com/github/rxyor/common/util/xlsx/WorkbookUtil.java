package com.github.rxyor.common.util.xlsx;

import com.github.rxyor.common.core.exception.CarpIOException;
import java.io.File;
import java.io.InputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

}
