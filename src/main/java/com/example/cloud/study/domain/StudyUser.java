package com.example.cloud.study.domain;

import com.example.cloud.oauth2.entity.SocialUserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "study_user")
@Getter
@Entity
@NoArgsConstructor
@IdClass(StudyUserId.class)
public class StudyUser {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private SocialUserEntity user;

    @Enumerated(EnumType.STRING)
    private StudyRole role;

    @Builder
    public StudyUser(Study study, SocialUserEntity user, StudyRole role) {
        this.study = study;
        this.user = user;
        this.role = role;
    }
}
