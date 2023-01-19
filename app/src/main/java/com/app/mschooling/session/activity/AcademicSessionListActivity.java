package com.app.mschooling.session.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.other.adapter.AcademicSessionListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventPublish;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.session.GetSessionResponse;
import com.mschooling.transaction.response.session.SessionResponse;
import com.mschooling.transaction.response.session.UpdateSessionResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class AcademicSessionListActivity extends BaseActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.sessionMigration)
    LinearLayout sessionMigration;
    EventPublish event;
    List<SessionResponse> response;
    AcademicSessionListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_session_list);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.academic_session));

        apiCallBack(getApiCommonController().academicSession());

        sessionMigration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PreSessionMigrationActivity.class));
            }
        });
    }

    @Subscribe
    public void academicSession(GetSessionResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response.getSessionResponses();
//            for (int i = 0; i < this.response.size(); i++) {
//                /*if (this.response.get(i).getSessionType().value().equals(Common.SessionType.CURRENT.value())){
//                    this.response.get(i).setSelected(true);
//                }*/
//                if (this.response.get(i).getName().equals(Preferences.getInstance(getApplicationContext()).getAcademicSession())) {
//                    this.response.get(i).setSelected(true);
//                } else {
//                    this.response.get(i).setSelected(false);
//                }
//            }
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new AcademicSessionListAdapter(this, this.response);
            recyclerView.setAdapter(adapter);
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void eventClick(EventPublish event) {
        this.event = event;

        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_accept_decline);
        dialog.setCancelable(false);
        TextView tittle = dialog.findViewById(R.id.tittle);
        TextView message = dialog.findViewById(R.id.message);
        TextView delete = dialog.findViewById(R.id.delete);
        TextView cancel = dialog.findViewById(R.id.cancel);

        message.setText(getString(R.string.are_you_sure_you_want_to) + " " + getString(R.string.change_session_small) + "?");
        tittle.setText(getString(R.string.change_session));
        delete.setText(getString(R.string.yes));
        cancel.setText(getString(R.string.no));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                apiCallBack(getApiCommonController().updateSession(event.getId()));
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
    public void updateSession(UpdateSessionResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            for (int i = 0; i < this.response.size(); i++) {
                this.response.get(i).setSelected(false);
            }
            this.response.get(event.getPosition()).setSelected(true);
            adapter.notifyDataSetChanged();
        } else {
            dialogError(response.getMessage().getMessage());
        }

    }
}