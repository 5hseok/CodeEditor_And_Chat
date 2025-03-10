package com.example.cloud.oauth2.dto;

public record KakaoUserInfoDto(Long id, String email, String nickname, String profileImageUrl) {

    public static KakaoUserInfoDto of(Long id, String email, String nickname, String profileImageUrl) {
        return new KakaoUserInfoDto(id, email, nickname, profileImageUrl);
    }
}