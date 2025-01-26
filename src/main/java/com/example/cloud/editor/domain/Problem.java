package com.example.cloud.editor.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "problem")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate problemDate;

    @Column(nullable = false)
    private String problemContent;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    // 테스트 케이스와의 연관 관계
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCase> testCases = new ArrayList<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProblem> userProblems = new ArrayList<>();

    @Builder
    public Problem(String title, LocalDate problemDate, Difficulty difficulty, String problemContent) {
        this.title = title;
        this.problemDate = problemDate;
        this.difficulty = difficulty;
        this.problemContent = problemContent;
    }


}