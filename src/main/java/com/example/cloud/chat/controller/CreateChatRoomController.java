package com.example.cloud.chat.controller;

import com.example.cloud.chat.dto.CreateChatRoomRequestDTO;
import com.example.cloud.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class CreateChatRoomController {

    private final ChatRoomService  chatRoomService;

    @PostMapping("/create")
    public void createChatRoom(@RequestBody
                                 CreateChatRoomRequestDTO requestDTO) {

        chatRoomService.createChatRoom(requestDTO);
    }
}
