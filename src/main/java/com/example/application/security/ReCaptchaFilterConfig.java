package com.example.application.security;

import com.example.application.service.ReCaptchaProperties;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReCaptchaFilterConfig {

    private final String secretkey;
    private final LoginAttemptService loginAttemptService;

    public ReCaptchaFilterConfig(ReCaptchaProperties properties, LoginAttemptService loginAttemptService) {
        this.secretkey = properties.getSecretkey();
        this.loginAttemptService = loginAttemptService;
    }

    @Bean
    public FilterRegistrationBean<Filter> reCaptchaFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ReCaptchaFilter(secretkey,loginAttemptService)); // Your secret key goes here
        registrationBean.addUrlPatterns("/login"); // Apply this to the login endpoint
        registrationBean.setOrder(1); // Run this filter before Spring Security
        return registrationBean;
    }
}
