package com.example.cloud.oauth2.config;

import com.example.cloud.oauth2.jwt.JWTFilter;
import com.example.cloud.oauth2.jwt.JWTUtil;
import com.example.cloud.oauth2.handler.CustomSuccessHandler;
import com.example.cloud.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // csrf 비활성화
        http.csrf((auth) -> auth.disable());

        // cors 설정
        http.cors(corsCustomizser -> corsCustomizser.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                configuration.setAllowedMethods(Collections.singletonList("*"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setMaxAge(3600L);

                configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                return configuration;
            }
        }));

        // FormLogin 비활성화
        http.formLogin((auth) -> auth.disable());

        // HttpBasic 인증방식 비활성화
        http.httpBasic((auth) -> auth.disable());

        // Jwt Filter 추가
        http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // ouath2Login()
        // oauth2Client()
        http.oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig    // 커스텀한 oauth2 로그인할 때 사용자 정보를 얻어올 플랫폼별 동작로직 등록
                        .userService(customOAuth2UserService))
                .successHandler(customSuccessHandler));     // 커스텀한 successHandler 등록

        // 경로 별 인가
        http
                .authorizeHttpRequests((auth) -> auth

                    .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().permitAll());


        // 세션 설정 - JWT 방식으로 인증정보를 다룰거라 세션 상태를 STATELESS로 관리해야 함.
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
