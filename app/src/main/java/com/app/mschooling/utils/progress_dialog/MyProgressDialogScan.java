package com.app.mschooling.utils.progress_dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.app.mschooling.com.R;


/**
 * Created by silence12 on 19/8/17.
 */

public class MyProgressDialogScan extends Dialog {
    static MyProgressDialogScan progressDialog;
    private com.victor.loading.rotate.RotateLoading rotateLoading;
    private ImageView image;

    public MyProgressDialogScan(Context a) {
        super(a);
    }

    public static MyProgressDialogScan getInstance(Activity activity) {
        progressDialog = new MyProgressDialogScan(activity);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static MyProgressDialogScan getInstance(Activity activity, boolean b) {
        progressDialog = new MyProgressDialogScan(activity);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static void setDismiss() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.custom_progress_dialog_scan);
        this.rotateLoading = (com.victor.loading.rotate.RotateLoading) findViewById(R.id.loading_spinner);
        this.image = (ImageView) this.findViewById(R.id.image);
        this.image.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.animation));
        this.rotateLoading.start();
    }
}
