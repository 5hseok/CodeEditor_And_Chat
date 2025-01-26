package com.example.cloud.oauth2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialUserDTO {
    private Long id;
    private String email;
    private String username;
    private String role;
}
