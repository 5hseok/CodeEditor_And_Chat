package com.example.cloud.editor.repository;

import com.example.cloud.editor.domain.StudyUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyUserRepository extends JpaRepository<StudyUser, Long> {
}
