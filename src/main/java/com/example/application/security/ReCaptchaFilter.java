package com.example.application.security;

import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;

public class ReCaptchaFilter implements Filter {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private final String secretKey;
    private final LoginAttemptService loginAttemptService;

    public ReCaptchaFilter(String secretKey, LoginAttemptService loginAttemptService) {
        this.secretKey = secretKey;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if ("/login".equals(request.getServletPath()) && "POST".equalsIgnoreCase(request.getMethod()) && loginAttemptService.isBlocked()) {
                String recaptchaResponse = request.getParameter("g-recaptcha-response");

                // Log all request parameters for debugging
                Enumeration<String> parameterNames = request.getParameterNames();
                System.out.println("ReCaptchaFilter: Received parameters:");
                while (parameterNames.hasMoreElements()) {
                    String paramName = parameterNames.nextElement();
                    System.out.println(paramName + " = " + request.getParameter(paramName));
                }

                // Check if the reCAPTCHA token exists
                if (recaptchaResponse == null || recaptchaResponse.isBlank()) {
                    System.out.println("ReCaptchaFilter: Missing 'g-recaptcha-response'. Rejecting request.");
                    //response.sendRedirect("/login?error=true");
                    //response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing reCAPTCHA response");
                    return;
                }

                // Validate the reCAPTCHA response
                boolean isValid = validateReCaptchaResponse(recaptchaResponse, request.getRemoteAddr());
                if (!isValid) {
                    System.out.println("ReCaptchaFilter: Invalid 'g-recaptcha-response'. Rejecting request.");
                    //response.sendRedirect("/login?error=true");
                    //response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid reCAPTCHA response");
                    return;
                }

            System.out.println("ReCaptchaFilter: Valid 'g-recaptcha-response'. Proceeding...");
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    private boolean validateReCaptchaResponse(String token, String remoteIp) throws IOException {
        String postData = "secret=" + URLEncoder.encode(secretKey, "UTF-8") +
                "&remoteip=" + URLEncoder.encode(remoteIp, "UTF-8") +
                "&response=" + URLEncoder.encode(token, "UTF-8");

        String result = ReCaptcha.doHttpPost(VERIFY_URL, postData);
        System.out.println("ReCaptchaFilter: Google API response - " + result);

        // Parse JSON response from Google
        JsonObject json = Json.parse(result);
        JsonValue successValue = json.get("success");

        if (successValue == null) {
            System.out.println("ReCaptchaFilter: Google API response missing 'success' field.");
            return false;
        }

        boolean isValid = successValue.asBoolean();
        System.out.println("ReCaptchaFilter: reCAPTCHA validation result: " + isValid);
        return isValid;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
