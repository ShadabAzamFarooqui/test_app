package com.app.mschooling.utils.progress_dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.app.mschooling.com.R;


/**
 * Created by silence12 on 19/8/17.
 */

public class MyProgressDialog extends Dialog {
    static MyProgressDialog progressDialog;
    private static com.victor.loading.rotate.RotateLoading rotateLoading;

    public MyProgressDialog(Context a) {
        super(a);
    }

    public static MyProgressDialog getInstance(Activity activity) {
        progressDialog = new MyProgressDialog(activity);
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    public static void setDismiss() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.custom_progress_dialog);
        this.rotateLoading = (com.victor.loading.rotate.RotateLoading) findViewById(R.id.loading_spinner);
        this.rotateLoading.start();
    }
}
