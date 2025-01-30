package com.example.cloud.study.domain;

import java.io.Serializable;
import java.util.Objects;

public class StudyUserId implements Serializable {
    private Long study;
    private Long user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudyUserId)) return false;
        StudyUserId that = (StudyUserId) o;
        return Objects.equals(study, that.study)
                && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(study, user);
    }
}
