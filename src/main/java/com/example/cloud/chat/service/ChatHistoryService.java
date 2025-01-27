package com.example.cloud.chat.service;

import com.example.cloud.chat.domain.MongoChatMessage;
import com.example.cloud.chat.domain.MongoChatRoom;
import com.example.cloud.chat.dto.ChatHistoryResponseDTO;
import com.example.cloud.chat.repository.MongoChatRepository;
import com.example.cloud.chat.repository.MongoChatRoomRepository;
import com.example.cloud.global.exception.BusinessException;
import com.example.cloud.global.exception.message.ErrorMessage;
import com.example.cloud.global.jwt.JwtTokenProvider;
import com.example.cloud.oauth2.entity.SocialUserEntity;
import com.example.cloud.oauth2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatHistoryService {

    private final MongoChatRoomRepository mongoChatRoomRepository;
    private final MongoChatRepository mongoChatRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public ChatHistoryResponseDTO getChatHistory(String studyName, String selectDate, String token) {
        log.info("Getting chat history for study: {} on date: {}", studyName, selectDate);

        // MongoDB에서 특정 studyName과 selectDate 토픽 정보를 가지는 채팅방 정보와 채팅방 내역을 조회
        String chatRoomName = "study:" + studyName + ":" + selectDate; // 채팅방 ID 조합

        // MongoDB에서 채팅방 정보 조회
        log.info("Getting chat room info for room: {}", chatRoomName);
        MongoChatRoom mongoChatRoom = mongoChatRoomRepository.findByChatRoomName(chatRoomName)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방 정보가 존재하지 않습니다."));

        // 요청한 사용자가 채팅방에 속해 있는지 확인
//        jwtUtil.validateToken(token)
//                .orElseThrow(() -> new IllegalArgumentException("토큰 정보가 유효하지 않습니다."));

        SocialUserEntity user = userRepository.findById(jwtTokenProvider.getUserFromJwt(token))
                .orElseThrow(() -> new BusinessException(ErrorMessage.NOT_FOUND_USER));

        // 속해 있지 않다면 예외 처리
        log.info("mongo chat room members: {}", mongoChatRoom.getMembers().get(2).getEmail());
        if (mongoChatRoom.getMembers().stream()
                .anyMatch(m -> m.getEmail().trim().equalsIgnoreCase(user.getEmail().trim()))) {
            log.info("User is in the chat room");
        } else {
            log.info("User not in chat room: {}", user.getEmail());
            throw new IllegalArgumentException("해당 사용자는 채팅방에 속해 있지 않습니다.");
        }

        log.info("Chat room info: {}", mongoChatRoom);

        //만약 selectDate가 24시간 이내라면 Redis에서 채팅 내역을 조회
            // Redis에서 특정 studyName과 selectDate 토픽 정보를 가지는 채팅방 정보와 채팅방 내역을 조회
                List<Object> chatMessages = redisTemplate.opsForList().range("chat:" + studyName + ":" + selectDate, 0, -1);
                log.info("Total Message in Redis : {}", chatMessages);
                if (chatMessages != null && !chatMessages.isEmpty()) {
                    return ChatHistoryResponseDTO.builder()
                            .chatHistory(chatMessages)
                            .build();
                }

                log.info("No chat messages found in Redis");

            try {
                // 해당 채팅방의 채팅 내역을 MongoDB에서 조회
                // 여기서 에러를 던지지 못함. 무조건 빈 리스트가 던져짐
                List<MongoChatMessage> mongoChatMessages = mongoChatRepository.findByChatRoomName(mongoChatRoom.getChatRoomName());

                log.info("Chat messages found in MongoDB: {}", mongoChatMessages);

                if (mongoChatMessages != null && !mongoChatMessages.isEmpty()) {
                    return ChatHistoryResponseDTO.builder()
                            .chatHistory(Collections.singletonList(mongoChatMessages))
                            .build();
                }

                log.info("No chat messages found in MongoDB");
                throw new IllegalArgumentException("해당 채팅방에 채팅 내역이 존재하지 않습니다.");

            } catch (Exception e) {
                log.error("몽고디비에서 조회할 수 없음: {}", e.getMessage());
                throw new IllegalArgumentException("해당 채팅방에 채팅 내역이 존재하지 않습니다.");
            }
    }
}
