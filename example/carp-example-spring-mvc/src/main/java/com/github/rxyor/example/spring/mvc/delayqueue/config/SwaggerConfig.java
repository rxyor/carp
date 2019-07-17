package com.github.rxyor.example.spring.mvc.delayqueue.config;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *<p>
 *SwaggerConfiguration
 *</p>
 *
 * @author liuyang
 * @date 2018-12-07 Fri 13:27:46
 * @since 1.0.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public Docket commonDocket() {
        return new Docket(
            DocumentationType.SWAGGER_2)
            .groupName("Api.class Group")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("巷子外的人",
            "https://blog.csdn.net/liuyanglglg", "rxyor@outlook.com");
        return new ApiInfoBuilder()
            .title("carp web mvc api")
            .contact(contact)
            .version("1.0")
            .build();
    }
}
