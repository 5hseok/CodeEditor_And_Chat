package com.example.cloud.security.dto.member.kakaologin.response;

import lombok.*;

@Builder
public record SuccessLoginResponseDto (
        Long id,
        String name,
        String email,
        String jwtToken
){
}
