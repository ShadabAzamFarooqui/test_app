package com.app.mschooling.base.fragment;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.enrollment.LandingPageActivity;
import com.app.mschooling.home.admin.activity.AdminMainActivity;
import com.app.mschooling.home.student.activity.StudentMainActivity;
import com.app.mschooling.home.teacher.activity.TeacherMainActivity;
import com.app.mschooling.utils.BottomDialogAdapter;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.tapadoo.alerter.Alerter;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public abstract class BaseFragment extends ApiCallFragment {
    public static final int REQUEST_CODE_PICKER = 1122;
    public static final int REQUEST_GALLERY = 11;
    public static final int REQUEST_CAMERA = 22;
    public static BaseFragment context;
    public final int REQUEST_CODE_ADHAR = 101;
    public final int REQUEST_CODE_PAN = 102;
    public boolean isKeyPadOpen;
    public Uri fileUri;
    public String imageName = "";



    public LinearLayout back;

    public EditText search;

    LinearLayout search_btn;
    LinearLayout close;
    TextView title;
    DialogPlus dialog = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void toolbarClick(View view, String tittle) {
        try {

            back = view.findViewById(R.id.back);
            search_btn = view.findViewById(R.id.search_btn);
            close = view.findViewById(R.id.close);
            search = view.findViewById(R.id.search);
            title = view.findViewById(R.id.title);
            title.setText(tittle);
            back.setOnClickListener(v -> {
                search.setText("");
                if (isKeyPadOpen) {
                    Helper.closeKeyPad(getActivity(), isKeyPadOpen);
//                    return;
                }
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();
                } else {
                    try {
                        if (AdminMainActivity.context != null) {
                            if (AdminMainActivity.context.isForeground()) {
                                if (!AdminMainActivity.context.navigation.getMenu().findItem(R.id.navigation_home).isChecked()) {
                                    AdminMainActivity.context.navigation.setSelectedItemId(R.id.navigation_home);
                                }
                            } else {
                                getActivity().finish();
                            }
                        }

                        if (TeacherMainActivity.context != null) {
                            if (TeacherMainActivity.context.isForeground()) {
                                if (!TeacherMainActivity.context.navigation.getMenu().findItem(R.id.navigation_home).isChecked()) {
                                    TeacherMainActivity.context.navigation.setSelectedItemId(R.id.navigation_home);
                                }
                            } else {
                                getActivity().finish();
                            }
                        }

                        if (StudentMainActivity.context != null) {
                            if (StudentMainActivity.context.isForeground()) {
                                if (!StudentMainActivity.context.navigation.getMenu().findItem(R.id.navigation_home).isChecked()) {
                                    StudentMainActivity.context.navigation.setSelectedItemId(R.id.navigation_home);
                                }
                            } else {
                                getActivity().finish();
                            }
                        }

                    } catch (Exception e) {
                        getActivity().finish();
                    }

                }
            });

            search_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    title.setVisibility(View.GONE);
                    search_btn.setVisibility(View.GONE);
                    search.setVisibility(View.VISIBLE);
                    close.setVisibility(View.VISIBLE);
                    Helper.openKeyPad(getActivity());
                    search.requestFocus();
                }
            });


            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    title.setVisibility(View.VISIBLE);
                    search_btn.setVisibility(View.VISIBLE);
                    search.setVisibility(View.GONE);
                    close.setVisibility(View.GONE);
                    Helper.closeKeyPad(getActivity(), isKeyPadOpen);
                    search.setText("");
                }
            });


            KeyboardVisibilityEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
                @Override
                public void onVisibilityChanged(boolean isOpen) {
                    isKeyPadOpen = isOpen;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void timeOut(String msg) {
        dialogFailed(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }




    public void fragmentSwitching(Fragment fragment, String url, String name, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url); // set your parameteres
        bundle.putString("name", name); // set your parameteres
        bundle.putString("position", String.valueOf(position));
        fragment.setArguments(bundle);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.addToBackStack(null).commit();
    }






    public String getFilename() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ParameterConstant.getFolderName());
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

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
//        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if (index == -1) {
                String data = contentUri.getPath().replace("external_files", "storage/emulated/0");
                return data;
            } else {
                return cursor.getString(index);
            }
        }
    }

    public String compressImage(Uri imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 2000.0f;
        float maxWidth = 1600.0f;
//        float maxHeight = 816.0f;
//        float maxWidth = 612.0f;
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
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public void dateDialog(View view, TextView textView) {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf1 = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.getDefault());
                textView.setText(sdf1.format(myCalendar.getTime()));

            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

   /* public void setAnimation(View view) {
        view.findViewById(R.id.mainLayout)
                .setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_bottom_centered));
    }*/

    public void dialogPlus() {
        try {


            DialogPlus dialog = DialogPlus.newDialog(getActivity())
                    .setHeader(R.layout.profile_header)
                    .setContentHolder(new GridHolder(2))
                    .setGravity(Gravity.BOTTOM)
                    .setAdapter(new BottomDialogAdapter(getActivity(), 2))
                    .setMargin(15, 0, 15, 20)
                    .setPadding(0, 0, 0, 50)
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                            Log.e(" position", " click of item isssssssssssssss" + position);
                            if (position == 0) {
                                //   choosefromgallery();
                                dialog.dismiss();
//                            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                            getIntent.setType("image/*");
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                pickIntent.setType("image/*");
                                startActivityForResult(pickIntent, REQUEST_GALLERY);
                            }
                            if (position == 1) {
                                //   takeImageFromCamera();
                                dialog.dismiss();
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (Build.VERSION.SDK_INT > 23) {
                                    fileUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".provider", Helper.getOutputMediaFile(MEDIA_TYPE_IMAGE, imageName));
                                } else {
                                    fileUri = Uri.fromFile(Helper.getOutputMediaFile(MEDIA_TYPE_IMAGE, imageName));
                                }
                                //   this line is to be used for android 9
                                List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                for (ResolveInfo resolveInfo : resInfoList) {
                                    String packageName = resolveInfo.activityInfo.packageName;
                                    getActivity().grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                }
                                intent.putExtra("output", fileUri);
                                startActivityForResult(intent, REQUEST_CAMERA);
                            }
                            if (position == 2) {
                                Toast.makeText(getActivity(), getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(DialogPlus dialog, View view) {
                            Log.e(" inside", " dialog is dismiss issssssssssssssss");
                            view.findViewById(R.id.footer_close_button);
                            dialog.dismiss();
                        }
                    })
                    .setFooter(R.layout.footer)
                    .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                    .create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dialogError(String message) {
        Alerter.create(getActivity())
                .setTitle(getString(R.string.sorry))
                .enableSwipeToDismiss()
                .setBackgroundColorRes(R.color.military_blue)
                .setText(message)
                .setIcon(R.drawable.app_icon)
                .setIconColorFilter(0)
                .show();

    }

    public void fragmentSwitching(Fragment fragment) {
        if (isKeyPadOpen) {
            Helper.closeKeyPad(getActivity(), isKeyPadOpen);
        }
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.commit();
    }








    public void dialogSuccess(String msg) {

        try {


            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_success);
            dialog.setCancelable(true);
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


    public void dialogSuccessFinish(String msg) {
        try {


            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_success);
            dialog.setCancelable(false);
            LinearLayout retry = dialog.findViewById(R.id.retry);
            TextView message = dialog.findViewById(R.id.message);
            message.setText(msg);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    getActivity().finish();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void dialogAcceptDecline(String text, Call callback) {
        try {
            text=Helper.capsFirstOtherInLower(text);
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_accept_decline);
            dialog.setCancelable(false);
            TextView tittle = dialog.findViewById(R.id.tittle);
            TextView message = dialog.findViewById(R.id.message);
            TextView delete = dialog.findViewById(R.id.delete);
            TextView cancel = dialog.findViewById(R.id.cancel);

            message.setText(getString(R.string.are_you_sure_you_want_to) +" "+  text.toLowerCase() + "?");
            tittle.setText(text);
            delete.setText(text);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void textChangeListener(EditText editText1, EditText editText2, CheckBox checkBox) {
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkBox.isChecked()) {
                    editText2.setText(editText1.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void spinnerListener(Spinner spinner1, Spinner spinner2, CheckBox checkBox) {
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (checkBox.isChecked()) {
                    spinner2.setSelection(position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        Preferences.getInstance(getContext()).clear();
        Intent intent = new Intent(getActivity(), LandingPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @SuppressLint("SetTextI18n")
    public void setHeaderImage(View view){
        ImageView  infoImg=view.findViewById(R.id.infoImg);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tagLine = view.findViewById(R.id.tagLine);
        if (ParameterConstant.isMSchooling(getContext())){
            infoImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.mschooling_logo));
        }else if (ParameterConstant.isLittleSteps(getContext())){
            infoImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.little_steps));
        }
        if (BuildConfig.DEBUG) {
            if (ParameterConstant.isMSchooling(getContext())){
                tagLine.setVisibility(View.VISIBLE);
                tagLine.setText(getString(R.string.your_digital_manager));
            }else if (ParameterConstant.isLittleSteps(getContext())){
                tagLine.setVisibility(View.GONE);
                tagLine.setText("");
            }
            tv_title.setText(getString(R.string.app_name)+" ("+ Preferences.getInstance(getContext()).getBuildType()+")");
        }else {
            if (ParameterConstant.isMSchooling(getContext())){
                tv_title.setText(getString(R.string.app_name));
                tagLine.setVisibility(View.VISIBLE);
                tagLine.setText(getString(R.string.your_digital_manager));
            }else if (ParameterConstant.isLittleSteps(getContext())){
                tv_title.setText(getString(R.string.app_name));
                tagLine.setVisibility(View.GONE);
                tagLine.setText("");
            }
        }

    }
}


