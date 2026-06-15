//package com.example.Family_life_backend.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * 全域 CORS 設定 讓 Angular http://localhost:4200 可以呼叫 Spring Boot
// * http://localhost:8080
// */
//@Configuration
//public class CorsConfig {
//
//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/**")
//						.allowedOriginPatterns("http://localhost:4200", "http://127.0.0.1:4200",
//								"https://*.ngrok-free.app", "https://*.ngrok-free.dev")
//						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*")
//						.allowCredentials(true);
//			}
//		};
//	}
//}