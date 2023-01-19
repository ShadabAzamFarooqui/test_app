package com.app.mschooling.help_support_report_feedback.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.feedback.AddFeedbackRequest;
import com.mschooling.transaction.response.feedback.AddFeedbackResponse;

import org.greenrobot.eventbus.Subscribe;

public class FeedBackActivity extends BaseActivity {

    ImageView happy;
    ImageView sad;
    ImageView unsure;
    ImageView love;

    TextView happyTxt;
    TextView sadTxt;
    TextView unsureTxt;
    TextView loveTxt;

    EditText writeReview;
    Button submit;
    String feedback="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);
        toolbarClick(getString(R.string.feedback));
        happy = findViewById(R.id.happy);
        sad = findViewById(R.id.sad);
        unsure = findViewById(R.id.unsure);
        love = findViewById(R.id.love);

        happyTxt = findViewById(R.id.happyTxt);
        sadTxt = findViewById(R.id.sadTxt);
        unsureTxt = findViewById(R.id.unsureTxt);
        loveTxt = findViewById(R.id.loveTxt);

        writeReview = findViewById(R.id.writeReview);
        submit = findViewById(R.id.submit);
        writeReview.setVisibility(View.GONE);

        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback="happy";
                writeReview.setVisibility(View.VISIBLE);
                happy.setImageDrawable(getResources().getDrawable(R.drawable.happy_selected));
                sad.setImageDrawable(getResources().getDrawable(R.drawable.upset_unselect));
                unsure.setImageDrawable(getResources().getDrawable(R.drawable.unsure_unselect));
                love.setImageDrawable(getResources().getDrawable(R.drawable.love_unselect));

                happyTxt.setTextColor(getResources().getColor(R.color.military_blue));
                sadTxt.setTextColor(getResources().getColor(R.color.grey));
                unsureTxt.setTextColor(getResources().getColor(R.color.grey));
                loveTxt.setTextColor(getResources().getColor(R.color.grey));
            }
        });

        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback="sad";
                writeReview.setVisibility(View.VISIBLE);
                happy.setImageDrawable(getResources().getDrawable(R.drawable.happy_unselected));
                sad.setImageDrawable(getResources().getDrawable(R.drawable.upset_selected));
                unsure.setImageDrawable(getResources().getDrawable(R.drawable.unsure_unselect));
                love.setImageDrawable(getResources().getDrawable(R.drawable.love_unselect));

                happyTxt.setTextColor(getResources().getColor(R.color.grey));
                sadTxt.setTextColor(getResources().getColor(R.color.military_blue));
                unsureTxt.setTextColor(getResources().getColor(R.color.grey));
                loveTxt.setTextColor(getResources().getColor(R.color.grey));
            }
        });

        unsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback="notsure";
                writeReview.setVisibility(View.VISIBLE);
                happy.setImageDrawable(getResources().getDrawable(R.drawable.happy_unselected));
                sad.setImageDrawable(getResources().getDrawable(R.drawable.upset_unselect));
                unsure.setImageDrawable(getResources().getDrawable(R.drawable.unsure_selected));
                love.setImageDrawable(getResources().getDrawable(R.drawable.love_unselect));

                happyTxt.setTextColor(getResources().getColor(R.color.grey));
                sadTxt.setTextColor(getResources().getColor(R.color.grey));
                unsureTxt.setTextColor(getResources().getColor(R.color.military_blue));
                loveTxt.setTextColor(getResources().getColor(R.color.grey));
            }
        });

        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback="love";
                writeReview.setVisibility(View.VISIBLE);
                happy.setImageDrawable(getResources().getDrawable(R.drawable.happy_unselected));
                sad.setImageDrawable(getResources().getDrawable(R.drawable.upset_unselect));
                unsure.setImageDrawable(getResources().getDrawable(R.drawable.unsure_unselect));
                love.setImageDrawable(getResources().getDrawable(R.drawable.love_selected));

                happyTxt.setTextColor(getResources().getColor(R.color.grey));
                sadTxt.setTextColor(getResources().getColor(R.color.grey));
                unsureTxt.setTextColor(getResources().getColor(R.color.grey));
                loveTxt.setTextColor(getResources().getColor(R.color.military_blue));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedback.equals("") || writeReview.getText().toString().equals("")) {
                   dialogError( getString(R.string.please_give_the_feedback));
                } else {
                    AddFeedbackRequest request=new AddFeedbackRequest();
                    request.setContent(writeReview.getText().toString());
//                    request.setTitle(Common.Feedback.getFeedback(feedback.toUpperCase()));
                    request.setFeedbackType(Common.FeedbackType.FEEDBACK);
                    apiCallBack(getApiCommonController().feedback( request));
                }
            }
        });
    }

    @Subscribe
    public void feedback(AddFeedbackResponse response){
        if (response.getStatus().value()==Status.SUCCESS.value()){
            dialogSuccessFinish(response.getMessage().getMessage());
        }else {
           dialogFailed(response.getMessage().getMessage());
        }
    }
}
