package com.app.mschooling.event_handler;

public class EventDeleteBase {
    String message;
    int position;

    public EventDeleteBase(int position) {
        this.position = position;
    }


    public EventDeleteBase(String message, int position) {
        this.message = message;
        this.position = position;
    }


    public String getMessage() {
        return message;
    }

    public int getPosition() {
        return position;
    }

}
