package com.example.cloud.chat.service;

import com.example.cloud.chat.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisSubscriber redisSubscriber;
    private final ConcurrentHashMap<String, ChannelTopic> topicCache = new ConcurrentHashMap<>();


    public String createOrGetTopic(String studyName, LocalDateTime createdDate) {
        String topicKey = "study:" + studyName + ":" + createdDate.toLocalDate();

        return topicCache.computeIfAbsent(topicKey, key -> {
            ChannelTopic newTopic = new ChannelTopic(topicKey);
            redisSubscriber.addTopicListener(newTopic);
            log.info("Created new topic and registered listener: {}", topicKey);
            return newTopic;
        }).getTopic();
    }

    // 메시지 발행
    public void publishMessage(LocalDateTime createdDate, ChatMessageDTO message) {
        String topicKey = createOrGetTopic(message.getStudyName(), createdDate);
        log.info("Publishing message to topic: {}", topicKey);

        redisTemplate.convertAndSend(topicKey, message);
    }
}
