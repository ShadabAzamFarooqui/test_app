package com.app.mschooling.base.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

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


public class ApiCallFragment<T> extends Fragment {


    @Override
    public void onResume() {
        super.onResume();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onPause() {
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Subscribe
    public void timeOut(String msg) {
        String cap = msg.substring(0, 1).toUpperCase() + msg.substring(1);
        dialogFailed(cap);
    }


    public Api getApiCommonController() {
        return ThisApp.getApiCommonController(getActivity());
    }




    public void apiCallBack(Call<T> callback) {
        if (!ConnectivityReceiver.isConnected()) {
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_internet);
            dialog.setCancelable(true);
            LinearLayout retry = (LinearLayout) dialog.findViewById(R.id.retry);
            LinearLayout cancel = (LinearLayout) dialog.findViewById(R.id.cancel);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    apiCallBack(callback);
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            MyProgressDialog.getInstance(getActivity()).show();
            callback.enqueue(new Local<T>());
        }

    }


    public void apiCallBackWithout(Call<T> callback) {
        if (!ConnectivityReceiver.isConnected()) {
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_internet);
            dialog.setCancelable(false);
            LinearLayout retry = (LinearLayout) dialog.findViewById(R.id.retry);
            LinearLayout cancel = (LinearLayout) dialog.findViewById(R.id.cancel);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    apiCallBackWithout(callback);
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();


        } else {
            callback.enqueue(new LocalWithout<T>());
        }

    }


    public void dialogNoInternet() {
        try {


            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_internet);
            dialog.setCancelable(false);
            LinearLayout retry = (LinearLayout) dialog.findViewById(R.id.retry);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ConnectivityReceiver.isConnected()) {
                        dialogNoInternet();
                    } else {
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dialogFailed(String msg) {
        try {


            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_failed);
            dialog.setCancelable(false);
            LinearLayout retry = dialog.findViewById(R.id.retry);
            TextView message = dialog.findViewById(R.id.message);
            message.setText(msg);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dialogLogout() {
        try {
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_logout);
            dialog.setCancelable(false);
            TextView logout = dialog.findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminMainActivity.context = null;
                    TeacherMainActivity.context = null;
                    StudentMainActivity.context = null;
                    Preferences.getInstance(getContext()).setLogin(false);
                    Preferences.getInstance(getContext()).setFirstTimeLogin(0);
                    Preferences.getInstance(getContext()).setToken("");
                    Intent intent = new Intent(getContext(), LandingPageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Local<T> implements Callback<T> {

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            MyProgressDialog.setDismiss();
            if (response.code() == 200) {
                T body = response.body();
                EventBus.getDefault().post(body);
            } else if (response.code() == 401 || response.code() == 403) {
                dialogLogout();
            } else {
                EventBus.getDefault().post(ParameterConstant.getErrorMessage() + " " + response.code());
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            MyProgressDialog.setDismiss();
            EventBus.getDefault().post(t.getMessage());
        }
    }

    class LocalWithout<T> implements Callback<T> {

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.code() == 200) {
                T body = response.body();
                EventBus.getDefault().post(body);
            } else if (response.code() == 401 || response.code() == 403) {
                dialogLogout();
            } else {
                EventBus.getDefault().post(ParameterConstant.getErrorMessage() + " " + response.code());
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            MyProgressDialog.setDismiss();
            EventBus.getDefault().post(t.getMessage());
        }
    }

}
