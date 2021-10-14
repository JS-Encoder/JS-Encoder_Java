package com.lzq.web;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import springfox.documentation.oas.annotations.EnableOpenApi;


//取消数据库自动配置
// @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
// 开启权限注解
@EnableGlobalMethodSecurity(securedEnabled = true)
//数据库密码加密注解
@EnableEncryptableProperties
@SpringBootApplication
@EnableOpenApi
@EnableDubbo
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
