package com.example.cloud.global.exception.constant;

public class Constant {

    public static final String[] AUTH_WHITE_LIST = {
            "/oauth/**",
            "/api/health",
            "/api/callback",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/login/kakao",
            "/ws/**"
    };
}

