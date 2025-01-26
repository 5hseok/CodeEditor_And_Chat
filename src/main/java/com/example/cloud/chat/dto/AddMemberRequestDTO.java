package com.example.cloud.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class AddMemberRequestDTO {
    private final String studyName;
    private final String createdAt;
    private final List<String> memberEmail;
}
