package com.example.cloud.chat.service;

import com.example.cloud.chat.domain.MongoChatRoom;
import com.example.cloud.chat.dto.AddMemberRequestDTO;
import com.example.cloud.chat.dto.CreateChatRoomRequestDTO;
import com.example.cloud.chat.repository.MongoChatRoomRepository;
import com.example.cloud.oauth2.entity.SocialUserEntity;
import com.example.cloud.oauth2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

    private final MongoChatRoomRepository mongoChatRoomRepository;
    private final UserRepository userRepository;
    // 채팅방 생성
    public void createChatRoom(CreateChatRoomRequestDTO requestDTO) {

        // 채팅방이 이미 존재하는 지 확인
        log.info("Checking if chat room already exists: {}", requestDTO.getStudyName() + ":" + requestDTO.getCreatedAt());
        MongoChatRoom chatRoom = mongoChatRoomRepository.findByChatRoomName("study:" + requestDTO.getStudyName() + ":" + requestDTO.getCreatedAt())
                .orElseGet(() -> {
                    // 없다면 해당 스터디이름과 당일 날짜로 채팅방 생성
                    log.info("Creating new chat room: {}", requestDTO.getStudyName() + ":" + requestDTO.getCreatedAt());

                    List<SocialUserEntity> userList = new ArrayList<>();
                    for (String memberEmail : requestDTO.getMemberEmailList()) {
                        SocialUserEntity user = userRepository.findByEmail(memberEmail)
                                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 정보가 존재하지 않습니다."));
                        userList.add(user);
                    }

                    return MongoChatRoom.builder()
                            .chatRoomName("study:" + requestDTO.getStudyName() + ":" + requestDTO.getCreatedAt())
                            .studyName(requestDTO.getStudyName())
                            .createdAt(LocalDate.parse(requestDTO.getCreatedAt()))
                            .members(userList)
                            .build();
                });

        log.info("Chat room created: {}", chatRoom);

        mongoChatRoomRepository.save(chatRoom);
    }

    // 채팅방 멤버 추가
    public void addMember(AddMemberRequestDTO requestDTO) {

        // 채팅방이 이미 존재하는 지 확인
        log.info("Checking if chat room already exists: {}", requestDTO.getStudyName() + ":" + requestDTO.getCreatedAt());
        MongoChatRoom chatRoom = mongoChatRoomRepository.findByChatRoomName("study:" + requestDTO.getStudyName() + ":" + requestDTO.getCreatedAt())
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));

        log.info("Adding new member to chat room: {}", requestDTO.getStudyName() + ":" + requestDTO.getCreatedAt());

        List<SocialUserEntity> userList = new ArrayList<>();
        for (String memberEmail : requestDTO.getMemberEmail()) {
            SocialUserEntity user = userRepository.findByEmail(memberEmail)
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자 정보가 존재하지 않습니다."));
            userList.add(user);
        }

        chatRoom.getMembers().addAll(userList);

        log.info("Member added to chat room: {}", chatRoom);

        mongoChatRoomRepository.save(chatRoom);
    }
}
