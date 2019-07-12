package com.github.rxyor.common.util.httclient;

import com.alibaba.fastjson.TypeReference;
import com.github.rxyor.common.core.model.R;
import java.util.HashMap;
import java.util.Map;
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
    public void testGet() {
        try {
            String url = "https://kunpeng.csdn.net/ad/template/237?positionId=62";
            R result = Request.url(url).get();
            System.out.println(result.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJsonGet() {
        try {
            String url = "http://local.home:9200/_search";
            String json = "{\n"
                + "  \"query\": {\n"
                + "    \"match_all\": {}\n"
                + "  }\n"
                + "}";
            R result = Request.url(url).body(json).get();
            System.out.println(result.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJsonGet2() {
        try {
            String url = "http://local.home:9200/_search";

            HashMap<String, Object> f2 = new HashMap<>();
            f2.put("match_all", new Object());

            HashMap<String, Object> json = new HashMap<>();
            json.put("query", f2);

            R result = Request.url(url).body(json).get();
            System.out.println(result.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJsonGet3() {
        try {
            String url = "http://local.home:9200/_search";

            HashMap<String, Object> f2 = new HashMap<>();
            f2.put("match_all", new Object());

            HashMap<String, Object> json = new HashMap<>();
            json.put("query", f2);

            R<Map<String, Object>> result = Request.url(url).body(json).dataType(new TypeReference<Map<String, Object>>() {
            }).get();
            System.out.println(result.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}