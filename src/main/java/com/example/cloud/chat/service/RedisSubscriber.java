package com.example.cloud.chat.service;

import com.example.cloud.chat.domain.MongoChatMessage;
import com.example.cloud.chat.dto.ChatMessageDTO;
import com.example.cloud.chat.repository.MongoChatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RedisSubscriber implements MessageListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;
    private final MongoChatRepository mongoChatRepository;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ConcurrentHashMap<String, ChannelTopic> subscribedTopics = new ConcurrentHashMap<>();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("Received message: {}", publishMessage);

            ChatMessageDTO chatMessage = objectMapper.readValue(publishMessage, ChatMessageDTO.class);
            log.info("Chat message received: {}", chatMessage);
            messagingTemplate.convertAndSend("/subscribe/chat/room/" + chatMessage.getStudyName(), chatMessage);
            log.info("Message [{}] sent to WebSocket subscribers", chatMessage.getMessage());

            try{
                // 채팅 내역을 Redis에 저장
                String redisKey = "chat:" + chatMessage.getStudyName() + ":" + chatMessage.getTimestamp().toLocalDate().toString();
                String serializedMessage = objectMapper.writeValueAsString(chatMessage);
                redisTemplate.opsForList().rightPush(redisKey,serializedMessage);
                // 메시지 TTL 설정 (10분 후 삭제)
                redisTemplate.expire(redisKey, Duration.ofMinutes(10));
                log.info("Total Message in Redis : {}", getChatMessages(redisKey));
            }
            catch (Exception e) {
                log.error("Error while saving chat message to Redis: {}", e.getMessage());
                throw new IllegalArgumentException("Error while saving chat message to Redis");
            }

            try {
                // 채팅 내역을 MongoDB에 저장
                mongoChatRepository.save(MongoChatMessage.builder()
                        .studyName(chatMessage.getStudyName())
                        .sender(chatMessage.getSender())
                        .message(chatMessage.getMessage())
                        .timestamp(chatMessage.getTimestamp().toString())
                        .chatRoomName("study:"+chatMessage.getStudyName()+":"+ chatMessage.getTimestamp().toLocalDate().toString())
                        .build());
            }
            catch (Exception e) {
                log.error("Error while saving chat message to MongoDB: {}", e.getMessage());
                throw new IllegalArgumentException("Error while saving chat message to MongoDB");
            }

        } catch (Exception e) {
            log.error("Error while processing chat message: {}", e.getMessage());
            throw new IllegalArgumentException("Error while processing chat message");
        }
    }

    public List<Object> getChatMessages(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public void addTopicListener(ChannelTopic topic) {
        if (subscribedTopics.putIfAbsent(topic.getTopic(), topic) == null) {
            redisMessageListenerContainer.addMessageListener(this, topic);
            log.info("Added new Redis topic listener: {}", topic.getTopic());
        }
    }
}