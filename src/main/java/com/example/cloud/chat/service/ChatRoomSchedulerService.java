package com.example.cloud.chat.service;

import com.example.cloud.chat.domain.MongoChatRoom;
import com.example.cloud.chat.repository.MongoChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomSchedulerService {

    private final MongoChatRoomRepository chatRoomRepository;

    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정 00:00:00에 실행
    public void createDailyChatRooms() {
        log.info("Running daily chat room creation job at {}", LocalDate.now());

        // 1. MongoDB에서 모든 studyName 가져오기 (중복 제거)
        List<String> studyNames = chatRoomRepository.findDistinctStudyNames();

        LocalDate today = LocalDate.now();

        // 2. 각 studyName에 대해 최신 날짜의 채팅방 조회 후 새로운 채팅방 생성
        List<MongoChatRoom> newChatRooms = studyNames.stream().map(studyName -> {
            // 최신 채팅방 찾기
            MongoChatRoom latestChatRoom = chatRoomRepository.findTopByStudyNameOrderByCreatedAtDesc(studyName)
                    .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다: " + studyName));

            // 새로운 채팅방 생성
            return MongoChatRoom.builder()
                    .id(new ObjectId())  // 새로운 ObjectId 생성
                    .chatRoomName("study:" + studyName + ":" + today)
                    .studyName(studyName)
                    .createdAt(today)
                    .members(latestChatRoom.getMembers())
                    .build();
        }).collect(Collectors.toList());

        // 3. 새로운 채팅방 저장
        chatRoomRepository.saveAll(newChatRooms);

        log.info("Created {} new chat rooms for date {}", newChatRooms.size(), today);
    }
}