package com.example.cloud.oauth2.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final SocialUserDTO socialUserDTO;

    public CustomOAuth2User(SocialUserDTO socialUserDTO) {
        this.socialUserDTO = socialUserDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return socialUserDTO.getRole();
            }
        });

        return collection;
    }

    public String getEmail() {
        return socialUserDTO.getEmail();
    }
    public Long getId(){
        return socialUserDTO.getId();
    }

    @Override
    public String getName() {
        return socialUserDTO.getUsername();
    }
}
