package com.example.cloud.editor.service;

import com.example.cloud.editor.domain.Problem;
import com.example.cloud.editor.dto.request.ProblemRequest;
import com.example.cloud.editor.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemRequest getProblemByDate(LocalDate date) {
        Problem problem = problemRepository.findByProblemDate(date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 문제가 없습니다."));

        return ProblemRequest.from(problem);
    }

    // 문제 생성, 업데이트 등 다른 비즈니스 로직 추가 가능
}
