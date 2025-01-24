package com.example.cloud.editor.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Operation(summary = "테스트", description = "스웨거 테스트용 컨트롤러입니다.")
    @GetMapping
    public String test() {
        return "테스트입니다";
    }
}
