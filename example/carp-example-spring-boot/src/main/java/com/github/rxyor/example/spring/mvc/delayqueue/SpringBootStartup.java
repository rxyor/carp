package com.github.rxyor.example.spring.mvc.delayqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-17 Wed 16:57:00
 * @since 1.0.0
 */
@ServletComponentScan
@SpringBootApplication
public class SpringBootStartup {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStartup.class);
    }
}
