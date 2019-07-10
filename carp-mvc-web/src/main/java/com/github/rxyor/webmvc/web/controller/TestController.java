package com.github.rxyor.webmvc.web.controller;

import com.github.rxyor.common.core.exception.CarpIOException;
import com.github.rxyor.common.core.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-02 Tue 09:44:00
 * @since 1.0.0
 */
@Slf4j
@Controller
@RequestMapping("/test")
public class TestController {

    @PostMapping("/exception")
    @ResponseBody
    public R testExceptional(@RequestParam("code") String code) {
        throw Exceptional.<CarpIOException>use(log).msg("code:%s error:", code).error();
//        return RUtil.success();
    }
}
