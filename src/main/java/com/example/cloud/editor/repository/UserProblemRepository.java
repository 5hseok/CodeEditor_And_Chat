package com.example.cloud.editor.repository;

import com.example.cloud.editor.domain.Problem;
import com.example.cloud.editor.domain.UserProblem;
import com.example.cloud.oauth2.entity.SocialUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProblemRepository extends JpaRepository<UserProblem, Long> {

    // 특정 유저가 특정 날짜의 문제를 풀었는지 확인
    Optional<UserProblem> findByUserAndProblem(SocialUserEntity user, Problem problem);
}
