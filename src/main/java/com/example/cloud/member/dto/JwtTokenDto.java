package com.example.cloud.member.dto;

import jakarta.validation.constraints.NotNull;

public record JwtTokenDto(

        @NotNull(message = "AccessToken은 null이 될 수 없습니다.")
        String accessToken,
        @NotNull(message = "RefreshToken은 null이 될 수 없습니다.")
        String refreshToken
) {
}
