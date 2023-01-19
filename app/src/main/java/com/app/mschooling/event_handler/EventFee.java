package com.app.mschooling.event_handler;

public class EventFee {
    String classId;
    String className;

    int position;

    public EventFee(String classId, String className) {
        this.classId = classId;
        this.className = className;
    }

    public EventFee(int position) {
        this.position = position;
    }

    public String getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public int getPosition() {
        return position;
    }
}
