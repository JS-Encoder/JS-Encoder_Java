package com.lzq.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
//开启Swagger
@EnableSwagger2
public class SwaggerConfig {

    //配置了Swagger的Docket的bean实例
    @Bean
    public Docket docket(Environment environment){

        return new Docket(DocumentationType.SWAGGER_2)
                //编写api的基本信息
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.lzq.web.controller"))
                //paths():过滤路径
                //PathSelectors.ant():只扫描扫描下的接口
                // .paths(PathSelectors.ant("/lzq/**"))
                .build()
                ;
    }


    //配置Swagger信息=apiInfo
    private ApiInfo apiInfo() {

        //作者信息
        Contact contact = new Contact("LZQ", "", "1275096074@qq.com");

        return new ApiInfo(
                "在线编译接口",
                "OnlineIDE接口",
                "v1.0",
                "",
                contact,
                "Apache 3.0",
                "",
                new ArrayList()
        );
    }



}
