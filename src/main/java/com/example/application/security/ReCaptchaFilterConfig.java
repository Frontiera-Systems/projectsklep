package com.example.application.security;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReCaptchaFilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> reCaptchaFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ReCaptchaFilter("6LfZUJ4qAAAAAG2DFv3Yaf19TlGcpNE0ipdJrL5v")); // Your secret key goes here
        registrationBean.addUrlPatterns("/login"); // Apply this to the login endpoint
        registrationBean.setOrder(1); // Run this filter before Spring Security
        return registrationBean;
    }
}
