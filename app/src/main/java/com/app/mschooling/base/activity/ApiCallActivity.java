package com.app.mschooling.base.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.mschooling.home.admin.activity.AdminMainActivity;
import com.app.mschooling.home.student.activity.StudentMainActivity;
import com.app.mschooling.home.teacher.activity.TeacherMainActivity;
import com.app.mschooling.enrollment.LandingPageActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.network.Api;
import com.app.mschooling.network.ThisApp;
import com.app.mschooling.utils.ConnectivityReceiver;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.progress_dialog.MyProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ApiCallActivity<T> extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void timeOut(String msg) {
        String cap = msg.substring(0, 1).toUpperCase() + msg.substring(1);
        dialogFailed(cap);
    }


    public Api getApiCommonController() {
        return ThisApp.getApiCommonController(this);
    }

    public void apiCallBack2(Call<T> callback) {

    }



    public void apiCallBack(Call<T> callback) {
        if (!ConnectivityReceiver.isConnected()) {
            Dialog dialog = new Dialog(this);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_internet);
            dialog.setCancelable(false);
            LinearLayout retry =  dialog.findViewById(R.id.retry);
            LinearLayout cancel =  dialog.findViewById(R.id.cancel);
            retry.setOnClickListener(v -> {
                dialog.dismiss();
                apiCallBack(callback);
            });
            cancel.setOnClickListener(v -> dialog.dismiss());
            dialog.show();

        } else {
            try {
                MyProgressDialog.setDismiss();
                MyProgressDialog.getInstance(this).show();
                callback.enqueue(new Local<>());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void apiCallBackWithout(Call<T> callback) {
//        this.callback=callback;
        if (!ConnectivityReceiver.isConnected()) {
            try {
                Dialog dialog = new Dialog(this);
                dialog.getWindow().requestFeature(1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setContentView(R.layout.dialog_internet);
                dialog.setCancelable(false);
                LinearLayout retry =  dialog.findViewById(R.id.retry);
                LinearLayout cancel =  dialog.findViewById(R.id.cancel);
                retry.setOnClickListener(v -> {
                    dialog.dismiss();
                    apiCallBackWithout(callback);
                });
                cancel.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            callback.enqueue(new LocalWithout<>());
        }

    }

    public void dialogLogout() {
        try {
            Dialog dialog = new Dialog(ApiCallActivity.this);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_logout);
            dialog.setCancelable(false);
            TextView logout = dialog.findViewById(R.id.logout);
            logout.setOnClickListener(v -> {
                AdminMainActivity.context = null;
                TeacherMainActivity.context = null;
                StudentMainActivity.context = null;
                Preferences.getInstance(getApplicationContext()).setLogin(false);
                Preferences.getInstance(getApplicationContext()).setFirstTimeLogin(0);
                Preferences.getInstance(getApplicationContext()).setToken("");
                Intent intent = new Intent(getApplicationContext(), LandingPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dialogFailed(String msg) {
        try {
            Dialog dialog = new Dialog(this);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_failed);
            dialog.setCancelable(false);
            LinearLayout retry = dialog.findViewById(R.id.retry);
            TextView message = dialog.findViewById(R.id.message);
            message.setText(msg);
            retry.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dialogFailedFinish(String msg) {
        try {
            Dialog dialog = new Dialog(this);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_failed);
            dialog.setCancelable(false);
            LinearLayout retry = dialog.findViewById(R.id.retry);
            TextView message = dialog.findViewById(R.id.message);
            message.setText(msg);
            retry.setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class Local<t> implements Callback<t> {

        @Override
        public void onResponse(@NonNull Call<t> call, Response<t> response) {
            MyProgressDialog.setDismiss();
            if (response.code() == 200) {
                t body = response.body();
                EventBus.getDefault().post(body);
            } else if (response.code() == 401 || response.code() == 403) {
                dialogLogout();
            } else {
                EventBus.getDefault().post(ParameterConstant.getErrorMessage() + " " + response.code());
            }
        }

        @Override
        public void onFailure(@NonNull Call<t> call, Throwable t) {
            MyProgressDialog.setDismiss();
            EventBus.getDefault().post(t.getMessage());
        }
    }

    class LocalWithout<t> implements Callback<t> {

        @Override
        public void onResponse(@NonNull Call<t> call, Response<t> response) {
            if (response.code() == 200) {
                EventBus.getDefault().post(response.body());
            } else if (response.code() == 401 || response.code() == 403) {
                dialogLogout();
            } else {
                EventBus.getDefault().post(ParameterConstant.getErrorMessage() + " " + response.code());
            }
        }

        @Override
        public void onFailure(@NonNull Call<t> call, @NonNull Throwable t) {
            try {
//                EventBus.getDefault().post(t.getMessage());
                EventBus.getDefault().post(getString(R.string.network_error_msg));
            } catch (Exception e) {
                EventBus.getDefault().post(getString(R.string.network_error_msg));
            }
        }
    }




}
