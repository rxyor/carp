package com.github.rxyor.common.util.xlsx;

import com.github.rxyor.common.util.io.FileUtil;
import com.github.rxyor.common.util.io.NIOUtil;
import com.github.rxyor.common.util.time.TimeUtil;
import com.github.rxyor.common.util.xlsx.test.Girl;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
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

    @Test
    public void export1() {
        String path = FileUtil.findRealPathByClasspath(this.getClass(), "/");
        path = path + "excel/Girl_" + TimeUtil.getCurrentSeconds() + ".xlsx";
        List<Girl> girls = this.mockGirls(2000);
        byte[] bytes = ExcelHelper.instance(Girl.class).input(girls).doExport();
        NIOUtil.writeFile(path, bytes);
    }

    @Test
    public void export2() {
        String path = FileUtil.findRealPathByClasspath(this.getClass(), "/");
        path = path + "excel/Girl_" + TimeUtil.getCurrentSeconds() + ".xlsx";
        List<Girl> girls = this.mockGirls(65588);
        byte[] bytes = ExcelHelper.instance(Girl.class).input(girls).doExport();
        File file = new File(path);
        try {
            IOUtils.copy(new ByteArrayInputStream(bytes), new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Girl> mockGirls(int size) {
        List<Girl> data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Girl girl = new Girl();
            girl.setName("CY" + i);
            girl.setAge(19);
            girl.setHeight(1.68F + 0.001F * i);
            girl.setWeight(45F + 0.001F * i);
            girl.setHairColor("黑色");
            girl.setEyeColor("灰色");
            data.add(girl);
        }

        return data;
    }
}