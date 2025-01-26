package com.example.cloud.editor.dto.request;

public record CodeRequest(
        String code,
        Long problemId,
        String language
) {
}
