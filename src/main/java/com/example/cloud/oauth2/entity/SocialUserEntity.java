package com.example.cloud.oauth2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserEntity {
    @Id
    private Long id;

    private String username;

    @Column(unique = true)
    private String email;

    private String role;

    private String refreshToken;

    public void updateEmail(String email){
        this.email = email;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
