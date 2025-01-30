package com.example.cloud.study.dto;

import java.util.List;

public record StudyInfoDto(
        Long userId,
        List<StudyMemberDto> memberList
) {
}
