package com.example.cloud.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class ChatHistoryResponseDTO {
    private final List<Object> chatHistory;
}

