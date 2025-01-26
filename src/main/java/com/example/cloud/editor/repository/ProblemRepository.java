package com.example.cloud.editor.repository;

import com.example.cloud.editor.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Optional<Problem> findByProblemDate(LocalDate date);
}
