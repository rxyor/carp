package com.github.rxyor.common.util.xlsx;

import com.github.rxyor.common.util.io.FileUtil;
import com.github.rxyor.common.util.xlsx.test.Girl;
import java.io.InputStream;
import java.util.List;
import org.junit.Test;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-14 Sun 03:51:00
 * @since 1.0.0
 */
public class ExcelHelperTest {

    @Test
    public void import1() {
        InputStream is = this.getClass().getResourceAsStream("/excel/Girl.xlsx");
        List<Girl> list = ExcelHelper.instance(Girl.class).input(is).doImport();
        System.out.println(list);
    }

    @Test
    public void import2() {
        String path = FileUtil.findRealPathByClasspath(this.getClass(), "/excel/Girl.xlsx");
        List<Girl> list = ExcelHelper.instance(Girl.class).input(path).useNIO(true).doImport();
        System.out.println(list);
    }
}