package com.lzq.web.config;

import com.lzq.web.intercepter.JWTIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTIntercepter()).addPathPatterns("/user/*","/example/*","/feedback/*","/query/getRecycle");
    }
}
