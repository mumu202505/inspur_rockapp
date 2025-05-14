package com.rockapp.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import com.rockapp.core.filter.JwtAuthenticationFilter;

@Configuration
public class FilterConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter; // 注入Spring管理的实例

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> registration() {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtFilter); // 使用Spring注入的实例
        registration.addUrlPatterns("/*");
        return registration;
    }

}
