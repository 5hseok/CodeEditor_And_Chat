package com.example.cloud.security.controller;

import com.example.cloud.global.exception.dto.SuccessStatusResponse;
import com.example.cloud.global.exception.dto.oauth.KakaoUserInfoRequestDto;
import com.example.cloud.global.exception.message.SuccessMessage;
import com.example.cloud.member.service.GeneralMemberService;
import com.example.cloud.security.dto.member.kakaologin.response.SuccessLoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GeneralMemberController {

    private final GeneralMemberService generalMemberService;
    //카카오 로그인 메소드
    @PostMapping("/login/kakao")
    public ResponseEntity<SuccessStatusResponse<SuccessLoginResponseDto>> login(@RequestBody KakaoUserInfoRequestDto userInfo) {
        // 3. 사용자 로그인 또는 회원가입 처리
        SuccessLoginResponseDto successLoginResponseDto = generalMemberService.findUserWithReact(userInfo);

        // 5. 메인 페이지로 리다이렉트
        return ResponseEntity.status(HttpStatus.OK).body(
                SuccessStatusResponse.of(
                        SuccessMessage.SIGNIN_SUCCESS, successLoginResponseDto
                )
        );
    }
}
