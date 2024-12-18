package com.example.application.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptService {
    private final int MAX_ATTEMPTS = 3; // Maksymalna liczba prób
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    // Zwiększenie liczby prób logowania
    public void loginFailed() {
        attemptsCache.put("captcha", attemptsCache.getOrDefault("captcha", 0) + 1);
    }

    // Reset liczby prób po udanym logowaniu
    public void loginSucceeded() {
        attemptsCache.remove("captcha");
    }

    // Sprawdzenie, czy użytkownik przekroczył limit prób
    public boolean isBlocked() {
        System.out.println(attemptsCache.getOrDefault("captcha", 0));
        return attemptsCache.getOrDefault("captcha", 0) >= MAX_ATTEMPTS;
    }
}
