package com.example.cloud.editor.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "test_case")
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(nullable = false)
    private String inputData;

    @Column(nullable = false)
    private String expectedOutput;

    @Builder
    public TestCase(String inputData, String expectedOutput) {
        this.inputData = inputData;
        this.expectedOutput = expectedOutput;
    }

}