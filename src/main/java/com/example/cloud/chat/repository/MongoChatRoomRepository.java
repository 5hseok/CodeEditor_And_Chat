package com.example.cloud.chat.repository;

import com.example.cloud.chat.domain.MongoChatRoom;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;
import java.util.Optional;

@EnableMongoRepositories
public interface MongoChatRoomRepository extends MongoRepository<MongoChatRoom, ObjectId> {
    Optional<MongoChatRoom> findByChatRoomName(String chatRoomName);

    // 모든 고유한 studyName 가져오기
    @Query(value = "{}", fields = "{'studyName': 1}")
    List<String> findDistinctStudyNames();

    // 특정 studyName의 최신 날짜 채팅방 가져오기
    Optional<MongoChatRoom> findTopByStudyNameOrderByCreatedAtDesc(String studyName);
}
