package com.example.application.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "recaptcha")
public class ReCaptchaProperties {

    private String sitekey;
    private String secretkey;
}
