package com.app.mschooling.event_handler;

public class UnAssignedQuizEvent {
    int classPosition, sectionPosition, subjectPosition, quizPosition;
    String id;

    public UnAssignedQuizEvent(int classPosition, int sectionPosition, int subjectPosition, int quizPosition, String id) {
        this.classPosition = classPosition;
        this.sectionPosition = sectionPosition;
        this.subjectPosition = subjectPosition;
        this.quizPosition = quizPosition;
        this.id = id;
    }

    public int getClassPosition() {
        return classPosition;
    }

    public int getSectionPosition() {
        return sectionPosition;
    }

    public int getSubjectPosition() {
        return subjectPosition;
    }

    public int getQuizPosition() {
        return quizPosition;
    }

    public String getId() {
        return id;
    }
}
