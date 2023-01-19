package com.app.mschooling.event_handler;

public class EventDelete {
    String id;
    String categoryId;
    int position;

    public EventDelete(int position) {
        this.position = position;
    }

    public EventDelete(String id, int position) {
        this.id = id;
        this.position = position;
    }

    public EventDelete(String categoryId, String id, int position) {
        this.categoryId = categoryId;
        this.id = id;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public int getPosition() {
        return position;
    }
}
