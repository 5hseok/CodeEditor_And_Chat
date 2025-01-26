package com.example.cloud.editor.dto.response;


import lombok.Builder;
import lombok.Getter;

@Builder
public record SubmitResponse(
    int code,                // 응답 코드 (e.g., 201)
    String message,          // 메시지 (e.g., "코드 제출 성공")
    boolean isSolve,         // 문제 풀이 성공 여부
    String resultMessage) {  // 실행 결과 메시지
}
