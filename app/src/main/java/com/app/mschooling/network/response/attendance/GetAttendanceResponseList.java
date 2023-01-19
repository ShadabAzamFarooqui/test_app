package com.app.mschooling.network.response.attendance;

import com.mschooling.transaction.common.api.Message;
import com.mschooling.transaction.common.api.Status;

import java.util.ArrayList;
import java.util.List;

public class GetAttendanceResponseList {

    private Status status;
    private Message message;
    private List<GetAttendanceResponse> attendanceResponses = new ArrayList();

    public GetAttendanceResponseList() {
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Message getMessage() {
        return this.message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<GetAttendanceResponse> getAttendanceResponses() {
        return this.attendanceResponses;
    }

    public void setAttendanceResponses(List<GetAttendanceResponse> attendanceResponses) {
        this.attendanceResponses = attendanceResponses;
    }
}
