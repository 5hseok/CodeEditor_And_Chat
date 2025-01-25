package com.example.cloud.chat.controller;

import com.example.cloud.chat.dto.ChatMessageDTO;
import com.example.cloud.chat.service.RedisPublisher;
import com.example.cloud.chat.service.StudyChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChannelTopic channelTopic;
    private final StudyChatService studyChatService;

        @MessageMapping("/room/{roomId}")
        @SendTo("/subscribe/chat/room/{roomId}")
        public void sendMessage(@DestinationVariable("roomId") Long roomId, @Valid ChatMessageDTO message) {
            log.info("id: {}, sendMessage: {}", roomId, message);
            System.out.println("Received roomId: " + roomId);
            System.out.println("Message received: " + message.getMessage());
            redisPublisher.publish(channelTopic, roomId, message);
        }

}