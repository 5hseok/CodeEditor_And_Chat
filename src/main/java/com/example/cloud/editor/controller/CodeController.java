package com.example.cloud.editor.controller;

import com.example.cloud.editor.dto.request.CodeRequest;
import com.example.cloud.editor.dto.response.CodeResponse;
import com.example.cloud.editor.dto.response.SubmitResponse;
import com.example.cloud.editor.service.CodeService;
import com.example.cloud.global.jwt.JwtTokenProvider;
import com.example.cloud.oauth2.entity.SocialUserEntity;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/code")
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "문제와 코드 기록 조회", description = "특정 날짜의 문제와 유저가 작성한 코드를 조회합니다.")
    @GetMapping
    public ResponseEntity<CodeResponse> getCode(
            @RequestHeader("Authorization") String token,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        Long userId = jwtTokenProvider.getUserFromJwt(token);

        CodeResponse codeResponse = codeService.getCode(userId, date);
        return ResponseEntity.ok(codeResponse);
    }

    @Operation(summary = "코드 제출", description = "유저의 코드를 제출하고 실행 결과를 반환합니다.")
    @PostMapping("/submit")
    public ResponseEntity<SubmitResponse> submitCode(
            @RequestHeader("Authorization") String token,
            @RequestBody CodeRequest codeRequest
    ) {
        Long userId = jwtTokenProvider.getUserFromJwt(token);
        SubmitResponse response = codeService.executeCode(userId, codeRequest);
        return ResponseEntity.ok(response);
    }
}