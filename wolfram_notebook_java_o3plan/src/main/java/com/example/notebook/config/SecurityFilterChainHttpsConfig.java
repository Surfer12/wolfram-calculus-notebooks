
package com.example.notebook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Security configuration with modern HTTPS standards and security headers.
 */
@Configuration
@EnableWebSecurity
public class SecurityFilterChainHttpsConfig {

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
                    .includeSubDomains(true)
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
                .contentTypeOptions(Customizer.withDefaults())

                // Referrer Policy - Control referrer information
                .referrerPolicy(referrerPolicy -> referrerPolicy
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))

                // Permissions Policy - Control browser features
                .permissionsPolicy(permissionsPolicy -> permissionsPolicy
                    .policy("camera=(), microphone=(), geolocation=(), payment=(), usb=()"))

                // Additional security headers - using custom header writers
                .and()
            );

        return http.build();
    }
}
