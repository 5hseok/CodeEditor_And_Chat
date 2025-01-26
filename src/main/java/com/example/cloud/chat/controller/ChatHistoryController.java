package com.example.cloud.chat.controller;

import com.example.cloud.chat.dto.ChatHistoryResponseDTO;
import com.example.cloud.chat.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    // 특정 날짜, 특정 스터디 채팅방의 채팅 내역 조회
    @GetMapping("/history")
    public ChatHistoryResponseDTO getChatHistory(@RequestParam String studyName,
                                                 @RequestParam String selectDate,
                                                 @CookieValue("Authorization") String token) {
        return chatHistoryService.getChatHistory(studyName, selectDate, token);
    }
}
