package com.example.cloud.chat.domain;

import com.example.cloud.oauth2.entity.SocialUserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "chat_rooms")
@Getter
@Builder
public class MongoChatRoom {
    @Id
    private ObjectId id;
    private String chatRoomName; // "study:" + studyName + ":" + createdAt
    private String studyName;
    private LocalDate createdAt;
    private List<SocialUserEntity> members;

    public void addMember(SocialUserEntity member) {
        this.members.add(member);
    }
}