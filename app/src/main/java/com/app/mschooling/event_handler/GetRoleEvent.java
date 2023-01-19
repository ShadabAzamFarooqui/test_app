package com.app.mschooling.event_handler;

public class GetRoleEvent {
    String enrollmentId;

    public GetRoleEvent(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }
}
