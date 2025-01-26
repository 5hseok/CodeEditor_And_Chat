package com.example.cloud.editor.dto.request;

import com.example.cloud.editor.domain.Problem;

import java.time.LocalDate;


public record ProblemRequest(
        Long problemId,
        LocalDate problemDate,
        String content
) {

    public static ProblemRequest from(Problem problem) {
        return new ProblemRequest(
                problem.getId(),
                problem.getProblemDate(),
                problem.getProblemContent()
        );
    }
}
