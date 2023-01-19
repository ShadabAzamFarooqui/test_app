package com.app.mschooling.add_teacher;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.app.mschooling.base.fragment.BaseFragment.REQUEST_CODE_PICKER;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.teacher.UpdateTeacherRequest;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.transaction.response.teacher.UpdateTeacherResponse;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.GridHolder;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.MultimediaPicker;
import com.mschooling.multimediapicker.model.Image;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddTeacherProfileImageActivity extends BaseActivity {

    UpdateTeacherRequest mRequest=new UpdateTeacherRequest();

    @BindView(R.id.captureImage)
    LinearLayout captureImage;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.submit)
    Button submit;

    String filePath;
    public Uri fileUri;
    public String imageName;
    public static final int REQUEST_GALLERY = 11;
    public static final int REQUEST_CAMERA = 22;
    @BindView(R.id.imageCapture)
    ImageView imageCapture;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    ArrayList<Image> imageList=new ArrayList<>();
    public static AddTeacherProfileImageActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher_profile_image);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.image));
        imageName=Helper.getRandom();
        context=this;
        image.setVisibility(View.GONE);
        captureImage.setVisibility(View.VISIBLE);

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogProfile();

                MultimediaPicker.create(AddTeacherProfileImageActivity.this)
                        .folderMode(true) // set folder mode (false by default)
                        .folderTitle(getString(R.string.album)) // folder selection title
                        .imageTitle(getString(R.string.tap_to_select)) // image selection title
                        .single() // single mode
                        .setOnlyImages(true)
                        .multi() // multi mode (default mode)
                        .limit(1) // max images can be selected (999 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                        .origin(imageList) // original selected images, used in multi mode
                        .start(REQUEST_CODE_PICKER);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath==null){
                    dialogError(getString(R.string.select_profile_image));
                    return;
                }

                multiPart();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                /*imageCapture.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                if (requestCode == REQUEST_CAMERA) {
                    fileUri = Uri.fromFile(Helper.getOutputMediaFile(MEDIA_TYPE_IMAGE, imageName));
                    Uri compressUri = Uri.parse(compressImage(fileUri));
                    filePath =compressUri.toString();
                    image.setImageURI(compressUri);
                }
                if (requestCode == REQUEST_GALLERY) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    fileUri = data.getData();
                    Cursor cursor = getContentResolver().query(fileUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    cursor.close();
                    Uri compressUri = Uri.parse(compressImage(fileUri));
                    filePath = compressUri.toString();
                    image.setImageURI(compressUri);
                }
                imageCapture.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);*/
                if (resultCode == RESULT_OK) {
                    imageCapture.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                    if (requestCode == REQUEST_CODE_PICKER) {
                        imageList = data.getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                        image.setImageURI(Uri.parse(imageList.get(0).getPath()));
                        filePath = compressImage(imageList.get(0).getPath()).toString();
                    }
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

    }



    @Subscribe
    public void getResourceResponse(AddResourceResponse r) {
        if (Status.SUCCESS.value() == r.getStatus().value()) {
            Firebase firebase = new Firebase();
            firebase.setUrl(r.getUrl());
            mRequest.getTeacherBasicRequest().setEnrollmentId(getIntent().getStringExtra("id"));
            mRequest.getTeacherProfileRequest().setProfileFirebase(firebase);
            apiCallBack(getApiCommonController().updateTeacher(mRequest));
        } else {
            dialogError(r.getMessage().getMessage());
        }
    }


    @Subscribe
    public void updateTeacher(UpdateTeacherResponse response) {
        if (response.getStatus().value()== Status.SUCCESS.value()) {
            Intent intent = new Intent(getApplicationContext(), AddTeacher2.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
            startActivity(intent);
        }else {
            dialogError(response.getMessage().getMessage());
        }
    }

    void multiPart() {
        File file = new File(filePath);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse("application/image"), file));
        apiCallBack(getApiCommonController().uploadResourceAdmin(Common.DocType.PROFILE, getIntent().getStringExtra("id"), filePart));
    }




    DialogPlus dialog = null;

    public void dialogProfile() {

        DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(this);
        dialogPlusBuilder.setHeader(R.layout.dialog_profile);
        dialogPlusBuilder.setContentHolder(new GridHolder(1));
        dialogPlusBuilder.setGravity(Gravity.BOTTOM);
        dialogPlusBuilder.setCancelable(true);

        dialogPlusBuilder.setMargin(5, 0, 5, 10);
        dialogPlusBuilder.setPadding(0, 0, 0, 10);

//        dialogPlusBuilder.setFooter(R.layout.footer);
        dialogPlusBuilder.setExpanded(false); // This will enable the expand feature, (similar to android L share dialog)
        dialog = dialogPlusBuilder.create();
        TextView tittle = (TextView) dialog.findViewById(R.id.tittle);
        tittle.setText(getString(R.string.profile));
        LinearLayout close = (LinearLayout) dialog.findViewById(R.id.close);
        LinearLayout camera = (LinearLayout) dialog.findViewById(R.id.camera);
        LinearLayout camera1 = (LinearLayout) dialog.findViewById(R.id.camera1);
        ImageView camera2 = (ImageView) dialog.findViewById(R.id.camera2);
        LinearLayout gallery = (LinearLayout) dialog.findViewById(R.id.gallery);
        LinearLayout gallery1 = (LinearLayout) dialog.findViewById(R.id.gallery1);
        ImageView gallery2 = (ImageView) dialog.findViewById(R.id.gallery2);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera();
            }
        });
        camera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera();
            }
        });
        camera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery();
            }
        });

        gallery1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery();
            }
        });
        gallery2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    void camera() {
        dialog.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageName = "PRO" + System.currentTimeMillis();
        if (Build.VERSION.SDK_INT > 23) {
            fileUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", Helper.getOutputMediaFile(MEDIA_TYPE_IMAGE, imageName));
        } else {
            fileUri = Uri.fromFile(Helper.getOutputMediaFile(MEDIA_TYPE_IMAGE, imageName));
        }
        //   this line is to be used for android 9
        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.putExtra("output", fileUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    void gallery() {
        /*imageName = "PRO" + System.currentTimeMillis();
        dialog.dismiss();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);*/
        imageName = "PRO" + System.currentTimeMillis();
        dialog.dismiss();
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, REQUEST_GALLERY);
    }
}
