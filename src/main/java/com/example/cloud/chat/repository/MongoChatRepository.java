package com.example.cloud.chat.repository;

import com.example.cloud.chat.domain.MongoChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;
import java.util.Optional;

@EnableMongoRepositories
public interface MongoChatRepository extends MongoRepository<MongoChatMessage, ObjectId> {
    List<MongoChatMessage> findByChatRoomName(String studyName);
}
