package com.bvv.filegeneration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de l'application
 */
@Configuration
public class AppConfig {

    /**
     * Configure le ObjectMapper pour Jackson
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

