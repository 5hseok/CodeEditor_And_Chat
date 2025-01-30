package com.example.cloud.editor.service;

import com.example.cloud.editor.domain.Problem;
import com.example.cloud.editor.domain.TestCase;
import com.example.cloud.editor.domain.UserProblem;
import com.example.cloud.editor.dto.request.CodeRequest;
import com.example.cloud.editor.dto.response.CodeResponse;
import com.example.cloud.editor.dto.response.SubmitResponse;
import com.example.cloud.editor.repository.ProblemRepository;
import com.example.cloud.editor.repository.TestCaseRepository;
import com.example.cloud.editor.repository.UserProblemRepository;
import com.example.cloud.oauth2.entity.SocialUserEntity;
import com.example.cloud.oauth2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeService {

    private final DockerService dockerService;
    private final UserRepository userRepository;
    private final UserProblemRepository userProblemRepository;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;

    /**
     * 코드 기록 조회
     * @param userId
     * @param date
     * @return
     */
    public CodeResponse getCode(Long userId, LocalDate date) {
        if(userId == null) {
            log.error("userId is null");
        }
        SocialUserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Problem problem = problemRepository.findByProblemDate(date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 문제가 없습니다."));

        UserProblem userProblem = userProblemRepository.findByUserAndProblem(user, problem).orElse(null);

        boolean isJoin = (userProblem != null);
        String code = (userProblem != null) ? userProblem.getCode() : "";
        boolean isSolve = (userProblem != null && Boolean.TRUE.equals(userProblem.getIsCorrect()));

        String solveTime = "";
        if (isSolve && userProblem != null && userProblem.getEndTime() != null && userProblem.getStartTime() != null) {
            long seconds = userProblem.getTime();
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;
            solveTime = String.format("%02d:%02d:%02d", hours, minutes, secs);
        }

        return CodeResponse.builder()
                .id(problem.getId())
                .isJoin(isJoin)
                .code(code)
                .content(problem.getProblemContent())
                .isSolve(isSolve)
                .solveTime(solveTime)
                .build();
    }

    /**
     * 코드 실행
     * @param userId
     * @param request
     * @return
     */
    public SubmitResponse executeCode(Long userId, CodeRequest request) {

        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));
        List<TestCase> testCases = testCaseRepository.findByProblemId(request.problemId());
        SocialUserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 기존 풀이 기록 확인 또는 새 기록 생성
        UserProblem userProblem = userProblemRepository.findByUserAndProblem(user, problem)
                .orElseGet(() -> UserProblem.builder()
                        .problem(problem)
                        .user(user)
                        .isCorrect(false)
                        .build());

        // 도커 컨테이너 생성
        String containerId = dockerService.createContainer(request.language());
        boolean isSolve = true;
        StringBuilder resultMessages = new StringBuilder();

        try {
            // 테스트 케이스 실행
            for (TestCase testCase : testCases) {
                String result = dockerService.executeCode(
                        containerId,
                        request.language(),
                        request.code(),
                        testCase.getInputData()
                );

                resultMessages.append("Input: ").append(testCase.getInputData()).append("\n")
                        .append("Expected: ").append(testCase.getExpectedOutput()).append("\n")
                        .append("Result: ").append(result).append("\n\n");

                if (!result.trim().equals(testCase.getExpectedOutput().trim())) {
                    isSolve = false;
                }
            }

            userProblem.startSolving();
            userProblem.submitCode(request.code());
            userProblem.endSolving();
            userProblem.checkAnswer(isSolve);
            userProblemRepository.save(userProblem);

            // 결과 반환
            return SubmitResponse.builder()
                    .code(201)
                    .message(isSolve ? "문제를 성공적으로 해결했습니다." : "문제 풀이에 실패했습니다.")
                    .isSolve(isSolve)
                    .resultMessage(resultMessages.toString())
                    .build();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("코드 실행 중 오류 발생: " + e.getMessage());
        } finally {
            // 컨테이너 정리
            dockerService.removeContainer(containerId);
        }
    }
}