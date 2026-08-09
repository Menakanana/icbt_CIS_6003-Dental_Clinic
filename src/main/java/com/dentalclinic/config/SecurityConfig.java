package com.dentalclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration Class.
 * 
 * Layer: Configuration Layer
 * Purpose:
 * 1. Configures the password hashing algorithm (BCrypt with cost factor 12).
 * 2. Configures web security filter chain (CSRF, endpoint permissions, frame options for H2 console).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Password Encoder Bean definition.
     * Uses BCrypt strong adaptive hashing algorithm with work factor 12.
     * 
     * @return PasswordEncoder instance used by AuthService and DataInitializer
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt hashing with strength 12 (one-way hashing, salted automatically)
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configures web security authorization rules and headers.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST APIs and simplified web form testing
            .csrf(AbstractHttpConfigurer::disable)
            // Define access permissions for URL patterns
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**", "/h2-console/**", "/**").permitAll()
                .anyRequest().authenticated()
            )
            // Allow same-origin frame rendering (required for H2 database console)
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            );
        return http.build();
    }
}
