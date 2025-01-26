package com.example.cloud.editor.controller;

import com.example.cloud.editor.dto.request.ProblemRequest;
import com.example.cloud.editor.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public ResponseEntity<?> getProblem(@RequestHeader("Authorization") String token,
                                        @RequestParam("date")
                                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        ProblemRequest problemDto = problemService.getProblemByDate(date);

        return ResponseEntity.ok(problemDto);
    }
}
