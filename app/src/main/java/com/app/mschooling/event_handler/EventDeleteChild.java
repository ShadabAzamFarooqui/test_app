package com.app.mschooling.event_handler;

public class EventDeleteChild {
    String message;
    int groupPosition;
    int childPosition;

    public EventDeleteChild(int position) {
        this.groupPosition = position;
    }

    public EventDeleteChild(int position, int childPosition) {
        this.groupPosition = position;
        this.childPosition = childPosition;
    }

    public EventDeleteChild(String message, int groupPosition) {
        this.message = message;
        this.groupPosition = groupPosition;
    }

    public EventDeleteChild(String message, int groupPosition, int childPosition) {
        this.message = message;
        this.groupPosition = groupPosition;
        this.childPosition = childPosition;
    }


    public String getMessage() {
        return message;
    }

    public int getGroupPosition() {
        return groupPosition;
    }

    public int getChildPosition() {
        return childPosition;
    }
}
