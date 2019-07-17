package com.github.rxyor.example.spring.mvc.delayqueue.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-17 Wed 09:58:00
 * @since 1.0.0
 */
@Controller
public class SwaggerController {

    @RequestMapping("/doc/api")
    public String forwardSwaggerUi() {
        return "redirect:/swagger-ui.html";
    }

}
