package com.example.cloud.member.service;

import com.example.cloud.global.exception.dto.oauth.KakaoUserInfoRequestDto;
import com.example.cloud.global.jwt.JwtTokenProvider;
import com.example.cloud.global.jwt.UserAuthentication;
import com.example.cloud.oauth2.entity.SocialUserEntity;
import com.example.cloud.oauth2.repository.UserRepository;
import com.example.cloud.security.dto.member.kakaologin.response.SuccessLoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class GeneralMemberService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    private static final String KAUTH_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAPI_USER_URL = "https://kapi.kakao.com/v2/user/me";
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 사용자 정보 가져오기 - 이메일, 닉네임, 프로필 사진, 여행 등급
    public SuccessLoginResponseDto findUserWithReact(KakaoUserInfoRequestDto userInfo) {

        SocialUserEntity socialUserEntity = userRepository.findById(userInfo.id())
                .orElseGet(() -> {
                    SocialUserEntity newGeneralMember = SocialUserEntity.builder()
                            .id(userInfo.id())
                            .username(userInfo.name())
                            .email(userInfo.email())
                            .role("ROLE_USER")
                            .build();
                    return userRepository.save(newGeneralMember);
                });

        // JWT 토큰 발급
        String jwtToken = jwtTokenProvider.issueAccessToken(
                UserAuthentication.createUserAuthentication(socialUserEntity.getId())
        );

        return SuccessLoginResponseDto.builder()
                .id(socialUserEntity.getId())
                .name(socialUserEntity.getUsername())
                .email(socialUserEntity.getEmail())
                .jwtToken(jwtToken)
                .build();
    }

}