package com.example.cloud.global.exception.dto.oauth;

import lombok.Builder;

@Builder
public record KakaoUserInfoRequestDto(
        String accessToken
) {
}
