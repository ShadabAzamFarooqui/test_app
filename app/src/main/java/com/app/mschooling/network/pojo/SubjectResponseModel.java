package com.app.mschooling.network.pojo;

import com.mschooling.transaction.response.subject.SubjectResponse;

import java.util.ArrayList;
import java.util.List;

public class SubjectResponseModel {
    String classId;
    String className;
    String description;
    List<SubjectResponse> mandatorySubject = new ArrayList<>();
    List<OptionSubject> optional = new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<SubjectResponse> getMandatorySubject() {
        return mandatorySubject;
    }

    public void setMandatorySubject(List<SubjectResponse> mandatorySubject) {
        this.mandatorySubject = mandatorySubject;
    }

    public List<OptionSubject> getOptional() {
        return optional;
    }

    public void setOptional(List<OptionSubject> optional) {
        this.optional = optional;
    }

    public static class OptionSubject {
        List<SubjectResponse> optionalSubject;
        String groupName;

        public List<SubjectResponse> getOptionalSubject() {
            return optionalSubject;
        }

        public void setOptionalSubject(List<SubjectResponse> optionalSubject) {
            this.optionalSubject = optionalSubject;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }
}
