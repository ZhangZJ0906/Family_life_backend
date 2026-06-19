package com.example.Family_life_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全域 CORS 設定 讓 Angular http://localhost:4200 可以呼叫 Spring Boot
 * http://localhost:8080
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOriginPatterns("*") // ⭐ 改這個
				.allowedMethods("*").allowedHeaders("*").allowCredentials(true);
	}
}