package com.solution.Ongi.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // POST 허용
            .formLogin(formLogin -> formLogin.disable()) // 기본 로그인 비활성화
            .httpBasic(httpBasic -> httpBasic.disable()) // http basic 비활성화
            .authorizeHttpRequests(auth -> auth
                // Swagger 경로는 인증 없이 허용
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // 모든 요청 허용
                .anyRequest().permitAll()
            );


        return http.build();
    }
}