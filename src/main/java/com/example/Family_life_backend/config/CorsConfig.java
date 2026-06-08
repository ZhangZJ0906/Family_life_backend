package com.example.Family_life_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全域 CORS 設定
 * 讓 Angular http://localhost:4200 可以呼叫 Spring Boot http://localhost:8080
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**")
                        // 允許 Angular 開發環境
                		.allowedOrigins("https://localhost:4200")
//                      .allowedOrigins("https://zipping-cytoplast-laxative.ngrok-free.dev")
//		                .allowedOriginPatterns(
//		                        "http://localhost:4200",
//		                        "http://127.0.0.1:4200",
//		                        "https://*.ngrok-free.dev",
//		                        "https://*.ngrok-free.app"
//		                    )

                        // 允許的 HTTP 方法
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                        // 允許所有 header
                        .allowedHeaders("*")

                        // 如果你沒有用 cookie / session，先用 false
                        .allowCredentials(false);
            }
        };
    }
}