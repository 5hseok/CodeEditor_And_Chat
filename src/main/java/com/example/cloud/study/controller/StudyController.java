package com.example.cloud.study.controller;

import com.example.cloud.global.jwt.JwtTokenProvider;
import com.example.cloud.study.dto.ResponseDto;
import com.example.cloud.study.dto.StudyInfoDto;
import com.example.cloud.study.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/study")
public class StudyController {

    private final JwtTokenProvider jwtTokenProvider;
    private final StudyService studyService;

    @Operation(summary = "스터디 참여 인원 조회")
    @GetMapping
    public ResponseEntity<ResponseDto<StudyInfoDto>> getUserStudy(@RequestHeader("Authorization") String token) {

        Long userId = jwtTokenProvider.getUserFromJwt(token);
        StudyInfoDto studyInfo = studyService.getStudy(userId);

        ResponseDto<StudyInfoDto> responseDto = ResponseDto.<StudyInfoDto>builder()
                .code(201)
                .message("/사용자의 스터디 정보를 성공적으로 조회")
                .data(studyInfo)
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

}
