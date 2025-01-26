package com.example.cloud.editor.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "study")
@Entity
@Getter
@NoArgsConstructor
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studyName;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyUser> studyUsers = new ArrayList<>();

    @Builder
    public Study(String studyName) {
        this.studyName = studyName;
    }

}
