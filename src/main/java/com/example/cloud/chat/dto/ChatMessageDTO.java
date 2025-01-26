package com.example.cloud.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatMessageDTO {
    private String studyName;
    private String sender;
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public ChatMessageDTO(String studyName, String sender, String message, LocalDateTime timestamp) {
        this.studyName = studyName;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }
}