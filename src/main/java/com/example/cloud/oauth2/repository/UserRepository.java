package com.example.cloud.oauth2.repository;

import com.example.cloud.oauth2.entity.SocialUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SocialUserEntity, Long> {
    SocialUserEntity findByUsername(String username);
    Optional<SocialUserEntity> findByEmail(String email);
}
