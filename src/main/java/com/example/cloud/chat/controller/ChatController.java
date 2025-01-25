package com.example.cloud.chat.controller;

import com.example.cloud.chat.dto.ChatMessageDTO;
import com.example.cloud.chat.service.RedisPublisher;
import com.example.cloud.chat.service.RedisSubscriber;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final RedisPublisher redisPublisher;

    // /publish/chat/room으로 발행
    // /subscribe/chat/room/{studyName}으로 구독
    @MessageMapping("/room")
    public void sendMessage( @Valid ChatMessageDTO message) {
        log.info("Message sent to study: {} on date: {}", message.getStudyName(), LocalDate.now());
        redisPublisher.publishMessage(LocalDateTime.now(), message);
    }
}