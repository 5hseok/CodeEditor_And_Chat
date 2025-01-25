package com.example.cloud.chat.service;
import com.example.cloud.chat.domain.ChatMessage;
import com.example.cloud.chat.dto.ChatMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RedisSubscriber implements MessageListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = new String(message.getBody());
            log.info("Received message: {}", publishMessage);

            ChatMessageDTO chatMessage = objectMapper.readValue(publishMessage, ChatMessageDTO.class);
            log.info("Deserialized ChatMessage: {}", chatMessage);

            messagingTemplate.convertAndSend("/subscribe/chat/room" + chatMessage.getRoomId(), chatMessage);
            log.info("Message [{}] sent by member: {} to chat room: {}", chatMessage.getMessage(), chatMessage.getSender(), chatMessage.getRoomId());
        } catch (Exception e) {
            log.error("Error while processing chat message: {}", e.getMessage());
        }
    }
}