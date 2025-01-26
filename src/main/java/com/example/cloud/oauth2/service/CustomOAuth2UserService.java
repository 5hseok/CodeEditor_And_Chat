package com.example.cloud.oauth2.service;

import com.example.cloud.oauth2.dto.*;
import com.example.cloud.oauth2.entity.SocialUserEntity;
import com.example.cloud.oauth2.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        // provider id를 이야기하는거임 (naver, google, ... )
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;
        if(registrationId.equals("naver")) {
            oAuth2Response = new OAuth2NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new OAuth2GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new OAuth2KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        SocialUserEntity existData = userRepository.findByUsername(username);
        if (existData == null) {
            SocialUserEntity socialUserEntity = new SocialUserEntity();
            socialUserEntity.setUsername(username);
            socialUserEntity.setEmail(oAuth2Response.getEmail());
            socialUserEntity.setRole("ROLE_USER");
            userRepository.save(socialUserEntity);

            SocialUserDTO socialUserDTO = new SocialUserDTO();
            socialUserDTO.setUsername(username);
            socialUserDTO.setId(socialUserEntity.getId());
            socialUserDTO.setEmail(oAuth2Response.getEmail());
            socialUserDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(socialUserDTO);
        } else {
            existData.setEmail(existData.getEmail());

            userRepository.save(existData);
            SocialUserDTO socialUserDTO = new SocialUserDTO();
            socialUserDTO.setUsername(existData.getUsername());
            socialUserDTO.setId(existData.getId());
            socialUserDTO.setEmail(existData.getEmail());
            socialUserDTO.setRole(existData.getRole());

            return new CustomOAuth2User(socialUserDTO);
        }


    }

}
