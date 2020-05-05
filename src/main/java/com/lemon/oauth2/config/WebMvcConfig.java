package com.lemon.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @param
 * @author lemon
 * @description
 * @return
 * @date 2019-08-18 20:11
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private HandlerInterceptor handlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可添加多个
        registry.addInterceptor(this.handlerInterceptor);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // TODO 设置login不会覆盖默认值
//        registry.addViewController("/login").setViewName("forward:/base-login.html");
//        registry.addViewController("/oauth/confirm_acces").setViewName("forward:/base-approval.html");
//
//        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}