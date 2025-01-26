package com.example.cloud.chat.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Document(collection = "chat_message_history")
@Builder
public class MongoChatMessage {
    @Id
    private ObjectId id;
    private String studyName;
    private String chatRoomName; // "study:" + studyName + ":" + createdAt
    private String sender;
    private String message;
    private String timestamp;

}