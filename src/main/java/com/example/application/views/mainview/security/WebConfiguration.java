package com.example.application.views.mainview.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Wyłączenie CSRF dla H2 Console
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").hasRole("ADMIN") // Dostęp tylko dla ADMIN
                        .anyRequest().authenticated() // Wszystkie inne żądania wymagają uwierzytelnienia
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions
                                .sameOrigin() // Pozwolenie na używanie ramek z tej samej domeny
                        )
                )
                .formLogin(withDefaults()); // Opcjonalnie: włączenie formularza logowania

        return http.build();
    }
}
