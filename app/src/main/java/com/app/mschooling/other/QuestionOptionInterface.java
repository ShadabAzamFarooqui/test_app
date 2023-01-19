package com.app.mschooling.other;

import com.mschooling.transaction.response.quiz.ChoiceResponse;

public interface QuestionOptionInterface {
    void selectIssue(int position, String answer, ChoiceResponse data);
}