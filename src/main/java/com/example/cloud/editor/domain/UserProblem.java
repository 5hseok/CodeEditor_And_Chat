package com.example.cloud.editor.domain;

import com.example.cloud.oauth2.entity.SocialUserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "user_problem")
@Entity
@Getter
@NoArgsConstructor
public class UserProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false)
    private Boolean isCorrect = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private SocialUserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Builder
    public UserProblem(String code, LocalDateTime startTime, LocalDateTime endTime, Boolean isCorrect, SocialUserEntity user, Problem problem) {
        this.code = code;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCorrect = isCorrect;
        this.user = user;
        this.problem = problem;
    }

    // 문제 푸는 데에 걸린 시간
    public long getTime() {
        if(startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).getSeconds();
        }
        return 0L;
    }


    // 도메인 메서드: 코드 설정
    public void submitCode(String code) {
        this.code = code;
    }

    // 도메인 메서드: 풀이 시작 시간 설정
    public void startSolving() {
        this.startTime = LocalDateTime.now();
    }

    // 도메인 메서드: 풀이 종료 시간 설정
    public void endSolving() {
        this.endTime = LocalDateTime.now();
    }

    // 도메인 메서드: 정답 여부 설정
    public void checkAnswer(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    // 도메인 메서드: 풀이 시간 계산
    public long calculateTimeTaken() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).getSeconds();
        }
        return 0L;
    }
}
