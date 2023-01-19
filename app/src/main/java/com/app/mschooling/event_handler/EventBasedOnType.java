package com.app.mschooling.event_handler;

import com.mschooling.transaction.common.api.Common;

public class EventBasedOnType {
    int position;
    String type;
    Common.WorksheetStatus worksheetStatus;

    public EventBasedOnType(String type, Common.WorksheetStatus worksheetStatus, int position) {
        this.type = type;
        this.position = position;
        this.worksheetStatus = worksheetStatus;

    }

    public EventBasedOnType( Common.WorksheetStatus worksheetStatus, int position) {
        this.worksheetStatus = worksheetStatus;
        this.position = position;

    }

    public int getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public Common.WorksheetStatus getWorksheetStatus() {
        return worksheetStatus;
    }
}
