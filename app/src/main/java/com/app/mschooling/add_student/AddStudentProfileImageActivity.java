package com.app.mschooling.add_student;

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
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.transaction.response.student.UpdateStudentResponse;
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

public class AddStudentProfileImageActivity extends BaseActivity {

    UpdateStudentRequest mRequest = new UpdateStudentRequest();

    @BindView(R.id.captureImage)
    LinearLayout captureImage;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.submit)
    Button submit;


    public static final int REQUEST_GALLERY = 11;
    public static final int REQUEST_CAMERA = 22;
    @BindView(R.id.imageCapture)
    ImageView imageCapture;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;
    @BindView(R.id.skipLayout)
    LinearLayout skipLayout;

    ArrayList<Image> imageList=new ArrayList<>();
    public static AddStudentProfileImageActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.tool_image));
        context = this;
        image.setVisibility(View.GONE);
        captureImage.setVisibility(View.VISIBLE);

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogProfile();

                MultimediaPicker.create(AddStudentProfileImageActivity.this)
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

        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddStudent2.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageList.size() == 0) {
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
            imageCapture.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            if (resultCode == RESULT_OK) {
                imageCapture.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                if (requestCode == REQUEST_CODE_PICKER) {
                    imageList = data.getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                    image.setImageURI(Uri.parse(imageList.get(0).getPath()));
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
            mRequest.getStudentBasicRequest().setEnrollmentId(getIntent().getStringExtra("id"));
            mRequest.getStudentProfileRequest().setProfileFirebase(firebase);
            apiCallBack(getApiCommonController().updateStudent(mRequest));
        } else {
            dialogError(r.getMessage().getMessage());
        }
    }


    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            Intent intent = new Intent(getApplicationContext(), AddStudent2.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
            startActivity(intent);
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    void multiPart() {
        File file = new File(compressImage(imageList.get(0).getPath()).toString());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse("application/image"), file));
        apiCallBack(getApiCommonController().uploadResourceAdmin(Common.DocType.PROFILE, getIntent().getStringExtra("id"), filePart));
    }



}
