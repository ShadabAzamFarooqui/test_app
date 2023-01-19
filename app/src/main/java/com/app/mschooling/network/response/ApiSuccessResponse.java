package com.app.mschooling.network.response;

import com.mschooling.transaction.common.api.Message;
import com.mschooling.transaction.common.api.Status;

public class ApiSuccessResponse {
    private static final long serialVersionUID = 2852641314922118860L;
    private Status status;
    private Message message;


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
}
