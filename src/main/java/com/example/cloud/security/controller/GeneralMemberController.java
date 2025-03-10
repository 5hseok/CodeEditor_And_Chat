package com.example.cloud.security.controller;

import com.example.cloud.global.exception.dto.SuccessStatusResponse;
import com.example.cloud.global.exception.dto.oauth.KakaoUserInfoRequestDto;
import com.example.cloud.global.exception.message.SuccessMessage;
import com.example.cloud.member.service.GeneralMemberService;
import com.example.cloud.security.dto.member.kakaologin.response.SuccessLoginResponseDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GeneralMemberController {

    private final GeneralMemberService generalMemberService;

    //카카오 로그인 메소드
    @PostMapping("/login/kakao")
    public ResponseEntity<SuccessStatusResponse<SuccessLoginResponseDto>> login(
            @RequestBody @NotNull KakaoUserInfoRequestDto userInfo
    ) {
        SuccessLoginResponseDto successLoginResponseDto = generalMemberService.kakaoAuthSocialLogin(userInfo);

        return ResponseEntity.status(HttpStatus.OK).body(
                SuccessStatusResponse.of(
                        SuccessMessage.SIGNIN_SUCCESS, successLoginResponseDto
                )
        );
    }

    // 액세스 토큰 재발급 및 리프레시 토큰 업데이트 API
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @NotNull @RequestHeader("Authorization") String refreshToken) {
        return ResponseEntity.status(HttpStatus.OK).body(
                generalMemberService.refresh(refreshToken)
        );

    }
}
