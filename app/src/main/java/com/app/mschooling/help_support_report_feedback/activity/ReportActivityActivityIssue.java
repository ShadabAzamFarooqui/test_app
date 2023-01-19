package com.app.mschooling.help_support_report_feedback.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.help_support_report_feedback.adapter.ReportIssueListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.network.pojo.ReportIssueModel;
import com.app.mschooling.other.ReportIssueInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.feedback.AddFeedbackRequest;
import com.mschooling.transaction.response.feedback.AddFeedbackResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ReportActivityActivityIssue extends BaseActivity implements ReportIssueInterface {
    private RecyclerView mRecyclerView;
    LinearLayout layoutOther, layoutCallUs;
    private Button mSubmit;
    ReportIssueListAdapter adapter;
    BottomSheetDialog dialog;
    private ArrayList<String> reportIssueList;
    private ReportIssueInterface issueReportInterface;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.report_issue_activity);
        issueReportInterface = this;
        reportIssueList = new ArrayList<>();
        toolbarClick(getString(R.string.report_an_issue));
        findIds();
        setListeners();
        setAdapter();

    }

    private void findIds() {
        mRecyclerView = findViewById(R.id.mRecyclerView);
        layoutOther = findViewById(R.id.layoutOther);
        layoutCallUs = findViewById(R.id.layoutCallUs);
        mSubmit = findViewById(R.id.submit);
    }

    private void setListeners() {
        layoutOther.setOnClickListener(v -> {
            reportIssueList.clear();
            setAdapter();
            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.botto_sheet_dialog_layout, null);
            TextView textName = view.findViewById(R.id.textName);
            TextView etEditText = view.findViewById(R.id.etEditText);
            TextView cancel = view.findViewById(R.id.cancel);
            TextView send = view.findViewById(R.id.send);
            textName.setText(getString(R.string.report_an_issue));
            etEditText.setHint(R.string.type_your_issue);
            dialog = new BottomSheetDialog(ReportActivityActivityIssue.this);
            dialog.setContentView(view);
            dialog.setCancelable(false);
            cancel.setOnClickListener(v1 -> {
                dialog.dismiss();
                dialog = null;
            });
            send.setOnClickListener(v12 -> {
                if (etEditText.getText().toString().equals("")) {
                    dialogError( getString(R.string.please_add_report_an_issue));
                } else {
                    AddFeedbackRequest request = new AddFeedbackRequest();
                    request.setContent(etEditText.getText().toString());
                    request.setFeedbackType(Common.FeedbackType.ISSUE);
                    apiCallBack(getApiCommonController().feedback( request));

                }
            });

            dialog.show();
        });
        layoutCallUs.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:0" + "9506228028"));
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(callIntent);
        });
        mSubmit.setOnClickListener(v -> {
            if (reportIssueList.size() == 0) {
                dialogError( getString(R.string.please_select_report_an_issue));
            } else {
                String str = "";
                for (int i = 0; i < reportIssueList.size(); i++) {
                    if (str.equals("")) {
                        str = reportIssueList.get(i);
                    } else {
                        str = str .concat( "," + reportIssueList.get(i));
                    }
                }
                AddFeedbackRequest request = new AddFeedbackRequest();
                request.setContent(str);
                request.setFeedbackType(Common.FeedbackType.ISSUE);
                apiCallBack(getApiCommonController().feedback( request));

            }
        });
    }

    private void setAdapter() {
        List<ReportIssueModel> list = new ArrayList<>();
        ReportIssueModel data;
        data = new ReportIssueModel();
        data.setName(getString(R.string.message_is_not_working));
        list.add(data);
        data = new ReportIssueModel();
        data.setName(getString(R.string.notification_is_not_working));
        list.add(data);
        data = new ReportIssueModel();
        data.setName(getString(R.string.fee_payment_not_working));
        list.add(data);
        data = new ReportIssueModel();
        data.setName(getString(R.string.student_details_not_working));
        list.add(data);
        data = new ReportIssueModel();
        data.setName(getString(R.string.not_able_to_add_remove_student));
        list.add(data);
        data = new ReportIssueModel();
        data.setName(getString(R.string.not_able_to_add_remove_teacher));
        list.add(data);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportIssueListAdapter(this, list, issueReportInterface);
        mRecyclerView.setAdapter(adapter);
    }

    @Subscribe
    public void feedback(AddFeedbackResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccessFinish( response.getMessage().getMessage());
            if (dialog != null) {
                setAdapter();
                dialog.dismiss();
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Override
    public void selectIssue(ArrayList<String> data) {
        reportIssueList = new ArrayList<>();
        reportIssueList.addAll(data);
        System.out.println(reportIssueList);

    }
}
