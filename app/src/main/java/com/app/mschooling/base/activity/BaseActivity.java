package com.app.mschooling.base.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.mschooling.add_student.AddStudent2;
import com.app.mschooling.add_student.AddStudent3;
import com.app.mschooling.add_student.AddStudentProfileImageActivity;
import com.app.mschooling.birthday.activity.BirthdayActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.enrollment.LandingPageActivity;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.event_handler.GetRoleEvent;
import com.app.mschooling.home.admin.activity.AdminMainActivity;
import com.app.mschooling.home.student.activity.StudentMainActivity;
import com.app.mschooling.home.teacher.activity.TeacherMainActivity;
import com.app.mschooling.students.detail.activity.StudentBasicDetailActivity;
import com.app.mschooling.teachers.detail.activity.TeacherDetailActivity;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.util.Base64Bridge;
import com.mschooling.transaction.common.util.StringEncoder;
import com.mschooling.transaction.response.configuration.DailyConfigurationResponse;
import com.mschooling.transaction.response.configuration.GetDailyConfigurationResponse;
import com.mschooling.transaction.response.configuration.GlobalEventResponse;
import com.mschooling.transaction.response.configuration.LocalEventResponse;
import com.mschooling.transaction.response.homework.UpdateStudentWorksheetResponse;
import com.mschooling.transaction.response.login.GetRoleResponse;
import com.mschooling.transaction.response.utility.GetAPIKeyResponse;
import com.sdsmdg.tastytoast.TastyToast;
import com.tapadoo.alerter.Alerter;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends ApiCallActivity {

    public static final int REQUEST_CREATE = 123;
    public static final int REQUEST_GALLERY = 11;
    public static final int REQUEST_CAMERA = 22;
    public static BaseActivity context;
    public final int REQUEST_CODE_ADHAR = 101;
    public final int REQUEST_CODE_PAN = 102;
    public EventDelete event;
    public Uri fileUri;
    public String imageName;

    public LinearLayout back;
    public LinearLayout search_btn;

    public EditText search;
    boolean isKeyPadOpen;
    LinearLayout close;
    TextView title;
    GetRoleEvent roleEvent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void toolbarClick(String tittle) {
        try {

            back = findViewById(R.id.back);
            search_btn = findViewById(R.id.search_btn);
            close = findViewById(R.id.close);
            search = findViewById(R.id.search);
            title = findViewById(R.id.title);
            title.setText(tittle);
            back.setOnClickListener(v -> {
                search.setText("");
                Helper.closeKeyPad(BaseActivity.this, isKeyPadOpen);
                try {
                    if (AdminMainActivity.context != null) {
                        if (AdminMainActivity.context.isForeground()) {
                            if (!AdminMainActivity.context.navigation.getMenu().findItem(R.id.navigation_home).isChecked()) {
                                AdminMainActivity.context.navigation.setSelectedItemId(R.id.navigation_home);
                            }
                        } else {
                            finish();
                        }
                    } else if (TeacherMainActivity.context != null) {
                        if (TeacherMainActivity.context.isForeground()) {
                            if (!TeacherMainActivity.context.navigation.getMenu().findItem(R.id.navigation_home).isChecked()) {
                                TeacherMainActivity.context.navigation.setSelectedItemId(R.id.navigation_home);
                            }
                        } else {
                            finish();
                        }
                    } else if (StudentMainActivity.context != null) {
                        if (StudentMainActivity.context.isForeground()) {
                            if (!StudentMainActivity.context.navigation.getMenu().findItem(R.id.navigation_home).isChecked()) {
                                StudentMainActivity.context.navigation.setSelectedItemId(R.id.navigation_home);
                            }
                        } else {
                            finish();
                        }
                    } else {
                        finish();
                    }
                } catch (Exception e) {
                    finish();
                }

            });

            search_btn.setOnClickListener(v -> {
                title.setVisibility(View.GONE);
                search_btn.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                close.setVisibility(View.VISIBLE);
                Helper.openKeyPad(BaseActivity.this);
                search.requestFocus();
            });


            close.setOnClickListener(v -> {
                title.setVisibility(View.VISIBLE);
                search_btn.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                Helper.closeKeyPad(BaseActivity.this, isKeyPadOpen);
                search.setText("");
            });


            KeyboardVisibilityEvent.setEventListener(this, isOpen -> isKeyPadOpen = isOpen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Uri compressImage(String imagePath) {
        String filePath = getRealPathFromURI(Uri.parse(imagePath));
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 1000.0f;
        float maxWidth = 800.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[64 * 1024];
//        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            assert scaledBitmap != null;
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            assert scaledBitmap != null;
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Uri.parse(filename);

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ParameterConstant.getFolderName());
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private String getRealPathFromURI(Uri contentUri) {
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if (index == -1) {
                return contentUri.getPath().replace("external_files", "storage/emulated/0");
            }
            return cursor.getString(index);
        }
    }

    public void dateDialog(View view, TextView textView) {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf1 = new SimpleDateFormat(ParameterConstant.getDateFormat(), Locale.getDefault());
            textView.setText(sdf1.format(myCalendar.getTime()));

        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    public void updateToken() {
        FirebaseApp.initializeApp(this);
//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
//            apiCallBackWithout(getApiCommonController().updateToken(instanceIdResult.getToken()));
//
//        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<String> task) {
                if (task.isSuccessful()) {
                    apiCallBackWithout(getApiCommonController().updateToken(task.getResult()));

                }
            }
        });

    }


    public void dialogError(String message) {
        Alerter.create(this)
                .setTitle(getResources().getString(R.string.sorry))
                .enableSwipeToDismiss()
                .setBackgroundColorRes(R.color.military_blue)
                .setText(message)
                .setIcon(R.drawable.app_icon)
                .setIconColorFilter(0)
                .show();
    }

    public void toastSuccess(String message) {
        TastyToast.makeText(getApplicationContext(), message, TastyToast.LENGTH_LONG, TastyToast.DEFAULT);

    }

    public void dialogUpdate(String msg, boolean isForceUpdate) {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_update);
        dialog.setCancelable(false);
        LinearLayout update = dialog.findViewById(R.id.update);
        LinearLayout cancel = dialog.findViewById(R.id.cancel);
        TextView message = dialog.findViewById(R.id.message);
        message.setText(msg);
        update.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(ParameterConstant.getPlayStoreUrl() + getApplicationContext().getPackageName()));
            startActivity(i);
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        if (isForceUpdate) {
            cancel.setVisibility(View.GONE);
        } else {
            cancel.setVisibility(View.VISIBLE);
        }
        dialog.show();
    }

    public void dialogSuccessAddUser(String type, String msg) {
        try {
            Dialog dialog = new Dialog(this);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_success);
            dialog.setCancelable(false);
            LinearLayout retry = dialog.findViewById(R.id.retry);
            TextView message = dialog.findViewById(R.id.message);
            message.setText(msg);
            retry.setOnClickListener(v -> {
                dialog.dismiss();
                if ("student".equals(type)) {
                    AddStudent2.context.finish();
                    AddStudentProfileImageActivity.context.finish();
                    AddStudent3.context.finish();
//                        AddStudent4.context.finish();
                    finish();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dialogSuccess(String msg) {
        try {
            Dialog dialog = new Dialog(this);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_success);
            dialog.setCancelable(true);
            LinearLayout retry = dialog.findViewById(R.id.retry);
            TextView message = dialog.findViewById(R.id.message);
            message.setText(msg);
            retry.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dialogSuccessFinish(String msg) {
        try {
            Dialog dialog = new Dialog(this);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_success);
            dialog.setCancelable(false);
            LinearLayout retry = dialog.findViewById(R.id.retry);
            TextView message = dialog.findViewById(R.id.message);
            message.setText(msg);
            retry.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    public void dialogEnableDisable(String text, Call callback) {
        try {
            Dialog dialog = new Dialog(this);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_accept_decline);
            dialog.setCancelable(false);
            TextView message = dialog.findViewById(R.id.message);
            TextView tittle = dialog.findViewById(R.id.tittle);
            TextView delete = dialog.findViewById(R.id.delete);
            TextView cancel = dialog.findViewById(R.id.cancel);
            message.setText(getString(R.string.are_you_sure) + " " + text.toLowerCase() + getString(R.string.question_symbol));
            tittle.setText(text);
            delete.setText(text);
            delete.setOnClickListener(v -> {
                dialog.dismiss();
                apiCallBack(callback);
            });
            cancel.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dialogAlert(String msg, String cancelTxt, String submitTxt, boolean clickable,
                            boolean hideCancel) {
        try {


            Dialog dialog = new Dialog(this);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_alert);
            dialog.setCancelable(clickable);
            TextView message = dialog.findViewById(R.id.message);

            TextView submit = dialog.findViewById(R.id.submit);
            TextView cancel = dialog.findViewById(R.id.cancel);

            cancel.setText(cancelTxt);
            submit.setText(submitTxt);
            message.setText(msg);

            if (hideCancel) {
                cancel.setVisibility(View.GONE);
            } else {
                cancel.setVisibility(View.VISIBLE);
            }


            submit.setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });
            cancel.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dialogReport(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.something_went_wrong);
        builder.setCancelable(false);
        builder.setPositiveButton(
                getString(R.string.report),
                (dialog, id) -> {
                    dialog.cancel();
                    apiCallBack(getApiCommonController().reportIssue(message));
                });

        builder.setNegativeButton(
                getString(R.string.cancel),
                (dialog, id) -> dialog.cancel());
        builder.setTitle(getString(R.string.sorry));
        builder.setIcon(R.drawable.without_m);
        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    public int getPosition(int arr, String input) {
        if (input == null) {
            input = "";
        }
        int position = 0;
        String[] array = getResources().getStringArray(arr);
        for (int i = 0; i < array.length; i++) {
            if (input.equals(array[i])) {
                position = i;
                return position;
            }

        }
        return position;
    }

    public void logoutEvent() {
        Preferences.getInstance(getApplicationContext()).clear();
        Intent intent = new Intent(getApplicationContext(), LandingPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public String getIntent(String key) {
        return getIntent().getStringExtra(key);
    }

    @Subscribe
    public void getRole(GetRoleEvent event) {
        this.roleEvent = event;
        if (!Preferences.getInstance(getApplicationContext()).getEnrollmentId().equals(event.getEnrollmentId())) {
            apiCallBack(getApiCommonController().getRole(roleEvent.getEnrollmentId()));
        }
    }

    @Subscribe
    public void getRoleResponse(GetRoleResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            if (Common.Role.TEACHER.value().equals(response.getRole().value())) {
                Intent intent = new Intent(getApplicationContext(), TeacherDetailActivity.class);
                intent.putExtra("id", roleEvent.getEnrollmentId());
                startActivity(intent);
            } else if (Common.Role.STUDENT.value().equals(response.getRole().value())) {
                Intent intent = new Intent(getApplicationContext(), StudentBasicDetailActivity.class);
                intent.putExtra("id", roleEvent.getEnrollmentId());
                startActivity(intent);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void getGoogleApiKey(GetAPIKeyResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            Preferences.getInstance(getApplicationContext()).setGooglePlaceApiKey(StringEncoder.decode(response.getApiKeyResponse().getGoogleAPIKey(), Base64Bridge.ANDROID_COMPATIBLE));
            Preferences.getInstance(getApplicationContext()).setYoutubeApiKey(StringEncoder.decode(response.getApiKeyResponse().getYoutubeAPIKey(), Base64Bridge.ANDROID_COMPATIBLE));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    //TODO: later on we will create a separate class.
    public boolean setError(LinearLayout linearLayout, TextView textView) {
        linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.error_border));

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                linearLayout.setBackground(ContextCompat.getDrawable(BaseActivity.this, R.drawable.gradient_border));
                textView.setHint(null);
                textView.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return false;
    }

    //TODO: later on we will create a separate class.
    public boolean setError(LinearLayout linearLayout, TextView textView, String error) {
        linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.error_border));
        textView.setHint(error);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                linearLayout.setBackground(ContextCompat.getDrawable(BaseActivity.this, R.drawable.gradient_border));
                textView.setHint(null);
                textView.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (!textView.getText().toString().isEmpty()) {
            textView.setError(error);
        }
        return false;
    }

    //TODO: later on we will create a separate class.
    public boolean setError(RelativeLayout relativeLayout, TextView textView, String error) {
        relativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.error_border));
        textView.setHint(error);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                relativeLayout.setBackground(ContextCompat.getDrawable(BaseActivity.this, R.drawable.gradient_border));
                textView.setHint(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return false;
    }


    //TODO: later on we will create a separate class.
    public boolean setError(LinearLayout linearLayout, EditText textView) {
        linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.error_border));

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                linearLayout.setBackground(ContextCompat.getDrawable(BaseActivity.this, R.drawable.gradient_border));
                textView.setHint(null);
                textView.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return false;
    }

    //TODO: later on we will create a separate class.
    public boolean setError(LinearLayout linearLayout, EditText textView, String error) {
        linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.error_border));
        textView.setHint(error);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                linearLayout.setBackground(ContextCompat.getDrawable(BaseActivity.this, R.drawable.gradient_border));
                textView.setHint(null);
                textView.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (!textView.getText().toString().isEmpty()) {
            textView.setError(error);
        }
        return false;
    }

    //TODO: later on we will create a separate class.
    public boolean setError(RelativeLayout relativeLayout, EditText textView, String error) {
        relativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.error_border));
        textView.setHint(error);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                relativeLayout.setBackground(ContextCompat.getDrawable(BaseActivity.this, R.drawable.gradient_border));
                textView.setHint(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (!textView.getText().toString().isEmpty()) {
            textView.setError(error);
        }
        return false;
    }


    public void dialogDelete(Call callback) {
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
    }

    public void getDailyConfiguration() {
        if (!Preferences.getInstance(getApplicationContext()).getApiCallDate().equals(Helper.getCurrentDate())) {
            apiCallBackWithout(getApiCommonController().getDailyConfigurationResponse());
        }
    }


    @Subscribe
    public void getDailyApiCallResponse(GetDailyConfigurationResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            Preferences.getInstance(getApplicationContext()).setApiCallDate(Helper.getCurrentDate());
            DailyConfigurationResponse dailyConfigurationResponse = response.getDailyConfigurationResponse();
            if (response.getDailyConfigurationResponse().isLocalEvent()) {
                for (int i = response.getDailyConfigurationResponse().getLocalEventResponses().size() - 1; i >= 0; i++) {
                    localGlobalEvent(
                            dailyConfigurationResponse.getLocalEventResponses().get(i).getName(),
                            dailyConfigurationResponse.getLocalEventResponses().get(i).getDescription()
                    );
                }
            }
            if (response.getDailyConfigurationResponse().isBirthDay()) {
                birthdayDialog(dailyConfigurationResponse.getBirthdayResponse().getUrl());
            }
            if (response.getDailyConfigurationResponse().isGlobalEvent()) {
                localGlobalEvent(
                        dailyConfigurationResponse.getGlobalEventResponse().getName(),
                        dailyConfigurationResponse.getGlobalEventResponse().getDescription()
                );
            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    void birthdayDialog(String url) {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_birthday);
        dialog.setCancelable(false);
        ShimmerFrameLayout shimmerFrameLayout = dialog.findViewById(R.id.shimmerFrameLayout);
        CircleImageView image = dialog.findViewById(R.id.image);
        LinearLayout close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        image.setImageDrawable(getResources().getDrawable(R.drawable.user));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(image);
    }


    void localGlobalEvent(String heading, String content) {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_local_global_event);
        dialog.setCancelable(false);
        TextView title = dialog.findViewById(R.id.title);
        TextView message = dialog.findViewById(R.id.message);
        LinearLayout close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        title.setText(heading);
        message.setText(content);
    }

}
