package com.app.mschooling.session.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.enrollment.LandingPageActivity;
import com.app.mschooling.home.admin.activity.AdminMainActivity;
import com.app.mschooling.home.student.activity.StudentMainActivity;
import com.app.mschooling.home.teacher.activity.TeacherMainActivity;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.CreateSessionRequest;
import com.mschooling.transaction.response.birthday.GetBirthdayResponse;
import com.mschooling.transaction.response.session.CreateSessionResponse;

import org.greenrobot.eventbus.Subscribe;

public class SessionMigrationActivity extends BaseActivity {

    EditText sessionName;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_migration);
        toolbarClick(getString(R.string.session_migration));
        sessionName = findViewById(R.id.sessionName);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(v -> {
            if (sessionName.getText().toString().trim().isEmpty()) {
                dialogError(getString(R.string.enter_session_name));
                return;
            }
            CreateSessionRequest request = new CreateSessionRequest();
            request.setName(sessionName.getText().toString().trim());
            apiCallBack(getApiCommonController().migrateSession(request));
        });
    }


    @Subscribe
    public void get(CreateSessionResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            Dialog dialog = new Dialog(SessionMigrationActivity.this);
            Window window=dialog.getWindow();
            window.requestFeature(1);
            window.setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_logout);
            dialog.setCancelable(false);
            TextView logout = dialog.findViewById(R.id.logout);
            TextView message = dialog.findViewById(R.id.message);
            TextView tittle = dialog.findViewById(R.id.tittle);
            logout.setText(getString(R.string.ok));
            message.setText(response.getMessage().getMessage().toString());
            tittle.setText(getString(R.string.success));

            logout.setOnClickListener(v -> {
                Preferences.getInstance(getApplicationContext()).setLogin(false);
                Preferences.getInstance(getApplicationContext()).setFirstTimeLogin(0);
                Preferences.getInstance(getApplicationContext()).setToken("");
                Intent intent = new Intent(getApplicationContext(), LandingPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
            dialog.show();

        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}