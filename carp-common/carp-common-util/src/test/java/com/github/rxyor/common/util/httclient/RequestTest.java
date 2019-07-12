package com.github.rxyor.common.util.httclient;

import com.github.rxyor.common.core.model.R;
import org.junit.Test;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-12 Fri 16:44:00
 * @since 1.0.0
 */
public class RequestTest {

    @Test
    public void get() {
        try {
            String url = "https://kunpeng.csdn.net/ad/template/237?positionId=62";
            R result = Request.url(url).get();
            System.out.println(result.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}