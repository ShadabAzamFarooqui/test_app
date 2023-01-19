package com.app.mschooling.unused.ocr.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.app.mschooling.com.R;

import java.util.ArrayList;


public class RunTimePermission  extends Activity {
    private Activity activity;
    private ArrayList<PermissionBean> arrayListPermission;
    private String[] arrayPermissions;
    private RunTimePermissionListener runTimePermissionListener;

    public RunTimePermission(Activity activity) {
        this.activity = activity;
    }

    public void requestPermission(String[] permissions, RunTimePermissionListener runTimePermissionListener) {
        this.runTimePermissionListener = runTimePermissionListener;
        this.arrayListPermission = new ArrayList();
        if (Build.VERSION.SDK_INT >= 23) {
            int i;
            for(i = 0; i < permissions.length; ++i) {
                PermissionBean permissionBean = new PermissionBean();
                if (ContextCompat.checkSelfPermission(this.activity, permissions[i]) == 0) {
                    permissionBean.isAccept = true;
                } else {
                    permissionBean.isAccept = false;
                    permissionBean.permission = permissions[i];
                    this.arrayListPermission.add(permissionBean);
                }
            }

            if (this.arrayListPermission.size() <= 0) {
                runTimePermissionListener.permissionGranted();
                return;
            }

            this.arrayPermissions = new String[this.arrayListPermission.size()];

            for(i = 0; i < this.arrayListPermission.size(); ++i) {
                this.arrayPermissions[i] = ((PermissionBean)this.arrayListPermission.get(i)).permission;
            }

            this.activity.requestPermissions(this.arrayPermissions, 10);
        } else if (runTimePermissionListener != null) {
            runTimePermissionListener.permissionGranted();
        }

    }

    private void callSettingActivity() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        Uri uri = Uri.fromParts("package", this.activity.getPackageName(), (String)null);
        intent.setData(uri);
        this.activity.startActivity(intent);
    }

    private void checkUpdate() {
        boolean isGranted = true;
        int deniedCount = 0;

        for(int i = 0; i < this.arrayListPermission.size(); ++i) {
            if (!((PermissionBean)this.arrayListPermission.get(i)).isAccept) {
                isGranted = false;
                ++deniedCount;
            }
        }

        if (isGranted) {
            if (this.runTimePermissionListener != null) {
                this.runTimePermissionListener.permissionGranted();
            }
        } else if (this.runTimePermissionListener != null) {
            this.setAlertMessage();
            this.runTimePermissionListener.permissionDenied();
        }

    }

    public void setAlertMessage() {
        AlertDialog.Builder adb;
        if (Build.VERSION.SDK_INT >= 21) {
            adb = new AlertDialog.Builder(this.activity, 16974394);
        } else {
            adb = new AlertDialog.Builder(this.activity);
        }

        adb.setTitle(this.activity.getResources().getString(R.string.app_name));
        String msg = getString(R.string.check_runtiome_permission_message) + this.activity.getResources().getString(R.string.app_name) + " Permission : Allow ALL</p>";
        adb.setMessage(Html.fromHtml(msg));
        adb.setPositiveButton(R.string.all_allow, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                RunTimePermission.this.callSettingActivity();
                dialog.dismiss();
            }
        });
        adb.setNegativeButton(R.string.remind_me_later, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (!this.activity.isFinishing() && msg.length() > 0) {
            adb.show();
        } else {
            Log.v("log_tag", "either activity finish or message length is 0");
        }

    }

    private void updatePermissionResult(String permissions, int grantResults) {
        for(int i = 0; i < this.arrayListPermission.size(); ++i) {
            if (((PermissionBean)this.arrayListPermission.get(i)).permission.equals(permissions)) {
                if (grantResults == 0) {
                    ((PermissionBean)this.arrayListPermission.get(i)).isAccept = true;
                } else {
                    ((PermissionBean)this.arrayListPermission.get(i)).isAccept = false;
                }
                break;
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for(int i = 0; i < permissions.length; ++i) {
            this.updatePermissionResult(permissions[i], grantResults[i]);
        }

        this.checkUpdate();
    }

    public interface RunTimePermissionListener {
        void permissionGranted();

        void permissionDenied();
    }

    public class PermissionBean {
        String permission;
        boolean isAccept;

        public PermissionBean() {
        }
    }
}
