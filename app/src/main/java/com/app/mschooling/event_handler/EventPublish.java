package com.app.mschooling.event_handler;

public class EventPublish {
    String id;
    int position;
    String action;


    public EventPublish(String id, int position) {
        this.id = id;
        this.position = position;
    }


    public EventPublish(String id, int position, String action) {
        this.id = id;
        this.position = position;
        this.action = action;
    }


    public String getId() {
        return id;
    }


    public int getPosition() {
        return position;
    }

    public String getAction() {
        return action;
    }
}
