package com.example.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RecaptchaService {
    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @Value("${recaptcha.verification-url}")
    private String verificationUrl;

    public boolean verifyCaptcha(String response) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> verificationResponse = restTemplate.postForObject(
                verificationUrl,
                Map.of("secret", secretKey, "response", response),
                Map.class
        );

        return verificationResponse != null && Boolean.TRUE.equals(verificationResponse.get("success"));
    }
}
