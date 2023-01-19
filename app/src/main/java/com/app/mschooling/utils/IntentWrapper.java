package com.app.mschooling.utils;

import com.mschooling.transaction.response.student.SectionResponse;

import java.util.List;

public class IntentWrapper {

    List<SectionResponse> sectionResponse;

    public IntentWrapper(List<SectionResponse> sectionResponse) {
        this.sectionResponse = sectionResponse;
    }


    public List<SectionResponse> getSectionResponse() {
        return sectionResponse;
    }

    public void setSectionResponse(List<SectionResponse> sectionResponse) {
        this.sectionResponse = sectionResponse;
    }
}
