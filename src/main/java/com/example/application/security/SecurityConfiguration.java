package com.example.application.security;

import com.example.application.service.ReCaptchaProperties;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    private final LoginAttemptService loginAttemptService;
   private final String secretkey;

    public SecurityConfiguration(LoginAttemptService loginAttemptService, ReCaptchaProperties properties) {
        this.loginAttemptService = loginAttemptService;
        this.secretkey = properties.getSecretkey();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new ReCaptchaFilter(secretkey,loginAttemptService), UsernamePasswordAuthenticationFilter.class);

        http

                .formLogin(form -> form.loginPage("/login")
                        .failureHandler(new CustomAuthenticationFailureHandler(loginAttemptService))
                        .successHandler(new CustomAuthenticationSuccessHandler(loginAttemptService))
                        .loginProcessingUrl("/login")
                        .permitAll());

        http.authorizeHttpRequests((authz) -> authz
                .requestMatchers(
                        new AntPathRequestMatcher("/VAADIN/**")
                ).permitAll()
        );

        http.csrf((csrf) ->
                csrf.ignoringRequestMatchers(
                        new AntPathRequestMatcher("/login")
                ).csrfTokenRepository(
                        CookieCsrfTokenRepository.withHttpOnlyFalse()
                )
        );
        http.headers((headers) -> headers
                .frameOptions(
                        HeadersConfigurer.FrameOptionsConfig::disable
                )
        );


        setLoginView(http, LoginView.class);
        super.configure(http);

    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        // Customize your WebSecurity configuration.
        super.configure(web);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

