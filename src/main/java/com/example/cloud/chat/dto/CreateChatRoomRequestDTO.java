package com.example.cloud.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@RequiredArgsConstructor
public class CreateChatRoomRequestDTO {

    private final String studyName;
    private final List<String> memberEmailList;
    private final String createdAt;
}
