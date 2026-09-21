package com.mednet.externalpacs.common.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${externalpacs.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration reg =
                registry.addMapping("/api/**")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
        String raw = allowedOrigins == null ? "*" : allowedOrigins.trim();
        if ("*".equals(raw)) {
            reg.allowedOrigins("*");
        } else {
            String[] origins =
                    Arrays.stream(raw.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .toArray(String[]::new);
            if (origins.length == 0) {
                reg.allowedOrigins("*");
            } else {
                reg.allowedOrigins(origins);
            }
        }
    }
}
