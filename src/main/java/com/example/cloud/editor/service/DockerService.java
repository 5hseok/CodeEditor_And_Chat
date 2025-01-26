package com.example.cloud.editor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class DockerService {

    private final ConcurrentHashMap<String, GenericContainer<?>> activeContainers = new ConcurrentHashMap<>();

    public String createContainer(String lang) {
        GenericContainer<?> container = switch (lang.toLowerCase()) {
            case "java" -> new GenericContainer<>("openjdk:22-ea-16-jdk")
                    .withCommand("sleep", "infinity");
            case "python" -> new GenericContainer<>("python:3.10.13-bookworm")
                    .withCommand("sleep", "infinity");
            default -> throw new IllegalArgumentException("Unsupported language: " + lang);
        };

        container.start();
        activeContainers.put(container.getContainerId(), container);
        return container.getContainerId();
    }

    public String executeCode(String containerId, String lang, String sourceCode, String input) throws IOException, InterruptedException {
        GenericContainer<?> container = activeContainers.get(containerId);

        // 1. 사용자 코드 파일 컨테이너에 복사
        String fileName = lang.equalsIgnoreCase("java") ? "Main.java" : "main.py";
        container.copyFileToContainer(
                Transferable.of(sourceCode.getBytes()),
                "/usr/src/" + fileName
        );

        // 2. 언어별로 컴파일 또는 바로 실행
        if (lang.equalsIgnoreCase("java")) {
            Container.ExecResult compileResult = container.execInContainer("javac", "/usr/src/Main.java");
            if (compileResult.getExitCode() != 0) {
                return compileResult.getStderr();
            }
        }

        // 3. 실행 명령어 (입력을 처리)
        Container.ExecResult result = lang.equalsIgnoreCase("java")
                ? container.execInContainer("sh", "-c", "echo \"" + input + "\" | java -cp /usr/src Main")
                : container.execInContainer("sh", "-c", "echo \"" + input + "\" | python3 /usr/src/main.py");

        return result.getExitCode() == 0 ? result.getStdout() : result.getStderr();
    }

    public void removeContainer(String containerId) {
        GenericContainer<?> container = activeContainers.remove(containerId);
        if (container != null) {
            container.stop();
        }
    }
}