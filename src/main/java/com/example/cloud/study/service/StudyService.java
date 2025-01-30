package com.example.cloud.study.service;

import com.example.cloud.oauth2.entity.SocialUserEntity;
import com.example.cloud.oauth2.repository.UserRepository;
import com.example.cloud.study.domain.Study;
import com.example.cloud.study.domain.StudyUser;
import com.example.cloud.study.dto.StudyInfoDto;
import com.example.cloud.study.dto.StudyMemberDto;
import com.example.cloud.study.repository.StudyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyUserRepository studyUserRepository;
    private final UserRepository userRepository;

    public StudyInfoDto getStudy(Long userId) {
        SocialUserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        StudyUser studyUser = studyUserRepository.findAllByUser(user).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 사용자가 참여 중인 스터디가 없습니다."));

        Study study = studyUser.getStudy();

        List<StudyMemberDto> memberList = study.getStudyUsers().stream()
                .map(su -> {
                    SocialUserEntity member = su.getUser();
                    return new StudyMemberDto(member.getUsername());
                })
                .collect(Collectors.toList());

        return new StudyInfoDto(study.getId(), memberList);
    }

}
