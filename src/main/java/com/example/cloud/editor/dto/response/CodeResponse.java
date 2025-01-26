package com.example.cloud.editor.dto.response;

import com.example.cloud.editor.domain.Problem;
import lombok.Builder;

@Builder
public record CodeResponse(
        Long id,
        boolean isJoin,
        String code,
        String content,
        boolean isSolve,
        String solveTime,
        String resultMessage
) {
}

