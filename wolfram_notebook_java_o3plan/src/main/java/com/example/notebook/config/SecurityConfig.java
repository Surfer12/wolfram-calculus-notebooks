package com.example.notebook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

/**
 * Security configuration with modern HTTPS standards and security headers.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for API endpoints (enable if you add web forms)
            .csrf(csrf -> csrf.disable())
            
            // Configure authorization
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/**", "/ws/**", "/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            
            // Security Headers - Highest Standards
            .headers(headers -> headers
                // HTTP Strict Transport Security (HSTS) - Force HTTPS
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000) // 1 year
                    .includeSubdomains(true)
                    .preload(true)
                )
                
                // Content Security Policy - Prevent XSS
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; " +
                                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                                    "style-src 'self' 'unsafe-inline'; " +
                                    "img-src 'self' data: blob:; " +
                                    "font-src 'self'; " +
                                    "connect-src 'self' ws: wss:; " +
                                    "frame-ancestors 'none'; " +
                                    "base-uri 'self'; " +
                                    "form-action 'self'")
                )
                
                // X-Frame-Options - Prevent clickjacking
                .frameOptions(frameOptions -> frameOptions.deny())
                
                // X-Content-Type-Options - Prevent MIME sniffing
                .contentTypeOptions(contentTypeOptions -> contentTypeOptions.and())
                
                // Referrer Policy - Control referrer information
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                
                // X-XSS-Protection - Enable XSS filtering
                .addHeaderWriter(new XXssProtectionHeaderWriter())
                
                // Permissions Policy - Control browser features
                .addHeaderWriter((request, response) -> {
                    response.setHeader("Permissions-Policy", 
                        "camera=(), microphone=(), geolocation=(), payment=(), usb=()");
                })
                
                // Cross-Origin Embedder Policy
                .addHeaderWriter((request, response) -> {
                    response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
                })
                
                // Cross-Origin Opener Policy
                .addHeaderWriter((request, response) -> {
                    response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
                })
                
                // Cross-Origin Resource Policy
                .addHeaderWriter((request, response) -> {
                    response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
                })
            );

        return http.build();
    }
}
