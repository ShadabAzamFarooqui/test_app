package com.app.mschooling.examination.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.examination.adapter.ExaminationListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.event_handler.EventPublish;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.examination.DeleteExaminationResponse;
import com.mschooling.transaction.response.examination.ExaminationResponse;
import com.mschooling.transaction.response.examination.GetExaminationResponse;
import com.mschooling.transaction.response.examination.PublishExaminationResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExaminationListActivity extends BaseActivity {


    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.add)
    LinearLayout add;
    List<ExaminationResponse> responseList;
    ExaminationListAdapter adapter;
    EventDelete event;
    EventPublish eventPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination_list);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.examination));

        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
            add.setVisibility(View.VISIBLE);
        } else {
            add.setVisibility(View.GONE);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateExaminationActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getExaminationListAdminRole(ParameterConstant.getRole(getApplicationContext())));
    }

    @Subscribe
    public void getExamList(GetExaminationResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            responseList = response.getExaminationResponses();
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            adapter = new ExaminationListAdapter(this, responseList, getIntent().getStringExtra("intentString"));
            recyclerView.setAdapter(adapter);

            if (response.getExaminationResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noRecord.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void deleteEvent(EventDelete event) {
        this.event = event;
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_delete_notice);
        dialog.setCancelable(false);
        TextView delete = dialog.findViewById(R.id.delete);
        TextView cancel = dialog.findViewById(R.id.cancel);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                apiCallBack(getApiCommonController().deleteExam(ParameterConstant.getRole(getApplicationContext()), event.getId()));
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();

    }

    @Subscribe
    public void publishEvent(EventPublish event) {
        this.eventPublish = event;
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_publish_result);
        dialog.setCancelable(false);
        TextView tittle = dialog.findViewById(R.id.tittle);
        TextView message = dialog.findViewById(R.id.message);
        ImageView image = dialog.findViewById(R.id.image);

        if (event.getAction().equals("UNPUBLISH")) {
            image.setImageDrawable(getDrawable(R.drawable.close));
            tittle.setText(getString(R.string.unpublish));
            message.setText(getString(R.string.unpublish_confirmation));
        } else {
            image.setImageDrawable(getDrawable(R.drawable.tick));
            tittle.setText(getString(R.string.publish));
            message.setText(getString(R.string.publish_confirmation));
        }
        TextView yes = dialog.findViewById(R.id.yes);
        TextView cancel = dialog.findViewById(R.id.cancel);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (event.getAction().equals("UNPUBLISH")) {
                    apiCallBack(getApiCommonController().publishExam(ParameterConstant.getRole(getApplicationContext()), event.getId(), false));
                } else {
                    apiCallBack(getApiCommonController().publishExam(ParameterConstant.getRole(getApplicationContext()), event.getId(), true));
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    @Subscribe
    public void delete(DeleteExaminationResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            responseList.remove(event.getPosition());
            adapter.notifyDataSetChanged();
            if (responseList.size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            } else {
                noRecord.setVisibility(View.GONE);
            }
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void published(PublishExaminationResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            if (eventPublish.getAction().equals("UNPUBLISH")) {
                responseList.get(eventPublish.getPosition()).setPublish(false);
            } else {
                responseList.get(eventPublish.getPosition()).setPublish(true);
            }
            adapter.notifyDataSetChanged();

            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


}
