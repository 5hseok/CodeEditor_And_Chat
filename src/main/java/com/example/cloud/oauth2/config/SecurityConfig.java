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
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of("https://codablesite.netlify.app"));
                    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
                    return configuration;
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
                        .requestMatchers("/login/oauth2/code/kakao/**").permitAll()
                        .anyRequest().authenticated());


        // 세션 설정 - JWT 방식으로 인증정보를 다룰거라 세션 상태를 STATELESS로 관리해야 함.
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
