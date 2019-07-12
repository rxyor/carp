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
        String url = "http://putuo.dev.dasouche.net/wholesale/shoppingCartController/queryAllItems.json";
        R result = Request.url(url)
            .header("_security_token", "04_W77p_LaM7ZYxocq")
            .get();
        System.out.println(result);
    }
}