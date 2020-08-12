package com.psh.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private static final String[] EXCLUDE_PATH = {
            "/##"
            , "/"
            , "/**.css"
            ,"/**.html"
            ,"/**.js"
            ,"/resources/**"
            ,"/api/login"
    };

    @Autowired
    private HandlerInterceptor handlerInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATH);

        WebMvcConfigurer.super.addInterceptors( registry );

    }
}
