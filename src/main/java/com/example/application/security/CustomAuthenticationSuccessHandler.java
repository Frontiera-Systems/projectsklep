package com.example.application.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginAttemptService loginAttemptService;

    public CustomAuthenticationSuccessHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Reset liczby prób
        String redirectUrl = request.getParameter("redirect");
        loginAttemptService.loginSucceeded();
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            // Przekieruj użytkownika do docelowego widoku
            response.sendRedirect(redirectUrl);
        } else {
            // Domyślne przekierowanie (np. na stronę główną)
            response.sendRedirect("/");
        }
    }
}
