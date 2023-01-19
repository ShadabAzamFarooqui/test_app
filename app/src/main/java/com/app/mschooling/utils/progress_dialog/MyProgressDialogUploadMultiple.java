package com.app.mschooling.utils.progress_dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mschooling.com.R;


/**
 * Created by silence12 on 19/8/17.
 */

public class MyProgressDialogUploadMultiple extends Dialog {
    static MyProgressDialogUploadMultiple progressDialog;
    static TextView message;
    static TextView count;
    static TextView total;
    private static LinearLayout cancel;
    private com.victor.loading.rotate.RotateLoading rotateLoading;

    public MyProgressDialogUploadMultiple(Context a) {
        super(a);
    }

    public static MyProgressDialogUploadMultiple getInstance(Activity activity) {
        progressDialog = new MyProgressDialogUploadMultiple(activity);
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    public static void setMessage(String msg) {
        message.setText(msg);
    }

    public static void setCount(String c, String t) {
        count.setText(c);
        total.setText("/" + t);
    }

    public static void setDismiss() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.custom_progress_dialog_upload_doc);
        this.rotateLoading = (com.victor.loading.rotate.RotateLoading) findViewById(R.id.loading_spinner);
        this.message = findViewById(R.id.message);
        this.count = findViewById(R.id.count);
        this.total = findViewById(R.id.total);
        this.cancel = findViewById(R.id.cancel);
        this.rotateLoading.start();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.dismiss();
            }
        });
    }
}
