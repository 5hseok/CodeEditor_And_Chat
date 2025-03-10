package com.example.cloud.oauth2.util;


import com.example.cloud.oauth2.dto.KakaoUserInfoDto;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class OAuth2Util {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String KAPI_USER_URL = "https://kapi.kakao.com/v2/user/me";

    public KakaoUserInfoDto getKakaoUserInfo(String accessToken) {

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Authorization", "Bearer " + accessToken);

        HttpEntity<?> kakaoProfileRequest = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        KAPI_USER_URL,
                        HttpMethod.POST,
                        kakaoProfileRequest,
                        String.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Kakao API 요청에 실패했습니다.");
        }

        JsonElement element = JsonParser.parseString(response.getBody());

        return KakaoUserInfoDto.of(
                element.getAsJsonObject().get("id").getAsLong(),
                element.getAsJsonObject().getAsJsonObject("kakao_account")
                        .get("email").getAsString(),
                element.getAsJsonObject().getAsJsonObject("kakao_account")
                        .getAsJsonObject("profile").get("nickname").getAsString(),
                element.getAsJsonObject().getAsJsonObject("kakao_account")
                        .getAsJsonObject("profile").get("profile_image_url").getAsString());
    }
}