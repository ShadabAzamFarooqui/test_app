package com.app.mschooling.help_support_report_feedback.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.feedback.AddFeedbackRequest;
import com.mschooling.transaction.response.feedback.AddFeedbackResponse;

import org.greenrobot.eventbus.Subscribe;

public class HelpActivity extends BaseActivity {
    private LinearLayout layoutReportIssue, layoutFeedback, layoutSuggestNewFeature, support;
    BottomSheetDialog dialog;


    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.help_activity);
        toolbarClick(getString(R.string.help));
        findIds();
        setListeners();
    }


    private void findIds() {
        layoutReportIssue = findViewById(R.id.layoutReportIssue);
        layoutFeedback = findViewById(R.id.layoutFeedback);
        layoutSuggestNewFeature = findViewById(R.id.layoutSuggestNewFeature);
        support = findViewById(R.id.support);
    }

    private void setListeners() {
        layoutReportIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, ReportActivityActivityIssue.class);
                startActivity(intent);
            }
        });
        layoutFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, FeedBackActivity.class);
                startActivity(intent);
            }
        });
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, ExternalSupportActivity.class);
                startActivity(intent);
            }
        });
        layoutSuggestNewFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.botto_sheet_dialog_layout, null);
                TextView textName = view.findViewById(R.id.textName);
                EditText etEditText = view.findViewById(R.id.etEditText);
                TextView cancel = view.findViewById(R.id.cancel);
                TextView send = view.findViewById(R.id.send);
                TextView attachment = view.findViewById(R.id.attachment);
                attachment.setVisibility(View.GONE);
                dialog = new BottomSheetDialog(HelpActivity.this);
                dialog.setContentView(view);
                dialog.setCancelable(false);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etEditText.getText().toString().equals("")) {
                            dialogError(getString(R.string.please_suggest_new_features));
                        } else {
                            AddFeedbackRequest request = new AddFeedbackRequest();
                            request.setContent(textName.getText().toString());
                            request.setFeedbackType(Common.FeedbackType.NEW_FEATURE);
                            apiCallBack(getApiCommonController().feedback( request));
                        }
                    }
                });

                dialog.show();
            }
        });
    }

    @Subscribe
    public void feedback(AddFeedbackResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccessFinish(response.getMessage().getMessage());
            dialog.dismiss();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}
