package com.ucdc.backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // Frontend local
        cfg.setAllowedOrigins(List.of("http://localhost:3000"));

        // Métodos permitidos (incluye OPTIONS para preflight)
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Encabezados permitidos
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Origin", "Accept"));

        // Encabezados expuestos (si necesitas leerlos desde el cliente)
        cfg.setExposedHeaders(List.of("Authorization", "Location"));

        // Solo si usas cookies/sesión; para Bearer tokens NO es necesario:
        cfg.setAllowCredentials(true);

        // Cache del preflight
        cfg.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
