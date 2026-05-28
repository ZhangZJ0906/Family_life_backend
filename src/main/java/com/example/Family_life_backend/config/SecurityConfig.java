package com.example.Family_life_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 設定
 * 如果專案有使用 Security，一定要開啟 cors()
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // 開啟 CORS，讓 CorsConfig 生效
            .cors(cors -> {})

            // 先關閉 CSRF，避免前端 POST 被擋
            .csrf(csrf -> csrf.disable())

            // 目前開發階段先允許所有 API
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/login", "/users/register").permitAll()
                .anyRequest().permitAll()
            );

        return http.build();
    }
}