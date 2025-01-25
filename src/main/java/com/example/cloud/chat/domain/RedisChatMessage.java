package com.example.cloud.chat.domain;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@RedisHash(value = "ChatMessage", timeToLive = 90000) // TTL 설정 (25시간)
public class RedisChatMessage {
    @Id
    private Long id;
    private Long roomId;
    private String sender;
    private String message;
    private LocalDateTime timestamp;

    public RedisChatMessage(Long roomId, String sender, String message) {
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}