package com.example.cloud.oauth2.entity;

import com.example.cloud.editor.domain.StudyUser;
import com.example.cloud.editor.domain.UserProblem;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserProblem> userProblems = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<StudyUser> studyUsers = new ArrayList<>();

    @Builder
    public SocialUserEntity(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
