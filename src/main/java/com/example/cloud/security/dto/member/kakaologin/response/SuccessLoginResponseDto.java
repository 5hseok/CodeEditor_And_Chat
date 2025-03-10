package com.example.cloud.security.dto.member.kakaologin.response;

import com.example.cloud.member.dto.JwtTokenDto;
import lombok.*;

@Builder
public record SuccessLoginResponseDto (
        Long id,
        String name,
        String email,
        JwtTokenDto jwtToken
){
}
