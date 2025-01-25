package com.example.cloud.chat.service;

import com.example.cloud.chat.domain.ChatMessage;
import com.example.cloud.chat.dto.ChatMessageDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, Long roomId, ChatMessageDTO chatMessage){
        log.info("Publishing message to topic: {}", topic.getTopic());
        ChatMessageDTO message = new ChatMessageDTO(roomId, chatMessage.getSender(), chatMessage.getMessage());
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
