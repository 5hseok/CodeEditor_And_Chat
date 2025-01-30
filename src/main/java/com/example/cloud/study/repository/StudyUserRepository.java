package com.example.cloud.study.repository;

import com.example.cloud.oauth2.entity.SocialUserEntity;
import com.example.cloud.study.domain.StudyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyUserRepository extends JpaRepository<StudyUser, Long> {

    List<StudyUser> findAllByUser(SocialUserEntity user);
}
