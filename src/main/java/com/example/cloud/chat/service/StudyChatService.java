package com.example.cloud.chat.service;

import com.example.cloud.chat.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudyChatService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    // 토픽 생성 및 존재 확인
    public String createOrGetTopic(String studyName, LocalDateTime createdDate) {
        String topicKey = "study:" + studyName + ":" + createdDate.toLocalDate();

        // 토픽이 존재하는지 확인
        Boolean exists = redisTemplate.hasKey(topicKey);

        if (Boolean.FALSE.equals(exists)) {
            log.info("Creating new topic: {}", topicKey);
            redisTemplate.convertAndSend(topicKey, "Topic created for study " + studyName);
        } else {
            log.info("Topic already exists: {}", topicKey);
        }
        return topicKey;
    }

    // 메시지 발행
    public void publishMessage(String studyName, LocalDateTime createdDate, ChatMessageDTO message) {
        String topicKey = createOrGetTopic(studyName, createdDate);
        redisTemplate.convertAndSend(topicKey, message);
    }
}
