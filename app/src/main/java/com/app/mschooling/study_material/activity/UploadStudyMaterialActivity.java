package com.app.mschooling.study_material.activity;

import static com.app.mschooling.base.fragment.BaseFragment.REQUEST_CODE_PICKER;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.study.AddStudyRequest;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.transaction.response.study.AddStudyResponse;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
//import com.mschooling.materialfilepicker.MaterialFilePicker;
//import com.mschooling.materialfilepicker.ui.FilePickerActivity;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadStudyMaterialActivity extends BaseActivity {

    LinearLayout imageLayout;
    Button submit;
    TextView path;
    EditText name;
    EditText priority;
    String url;
    String id;
    String categoryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_syllabus);

        submit = findViewById(R.id.submit);
        imageLayout = findViewById(R.id.imageLayout);
        path = findViewById(R.id.path);
        name = findViewById(R.id.name);
        priority = findViewById(R.id.priority);
        categoryId=getIntent().getStringExtra("categoryId");
        id=getIntent().getStringExtra("id");
        if (getIntent().getStringExtra("action")==null){
            toolbarClick(getString(R.string.add_study_material));
        }else {
            toolbarClick(getString(R.string.update_study_material));
            url=getIntent().getStringExtra("url");
            name.setText(getIntent().getStringExtra("name"));
            priority.setText(getIntent().getStringExtra("priority"));
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url == null||url.trim().isEmpty()) {
                    dialogError(getString(R.string.please_insert_pdf));
                } else if (name.getText().toString().trim().isEmpty() ) {
                    dialogError(getString(R.string.enter_name));
                } else {
                    Firebase firebase = new Firebase();
                    firebase.setName(name.getText().toString());
                    firebase.setId(id);
                    firebase.setUrl(url);
                    firebase.setFirebaseType(Common.FirebaseType.PDF);

                    AddStudyRequest request = new AddStudyRequest();
                    request.setId(id);
                    request.setName(name.getText().toString());
                    request.setUrl(url);
                    request.setCategoryId(categoryId);
                    if (priority.getText().toString().trim().isEmpty()) {
                        request.setPriority(0);
                    }else {
                        request.setPriority(Integer.parseInt(priority.getText().toString().trim()));
                    }
                    apiCallBack(getApiCommonController().addStudyMaterial(request));
                }
            }
        });
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    if (!Environment.isExternalStorageManager()) {
//                        Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                        startActivity(permissionIntent);
//                    } else {
//                        picker();
//                    }
//                }else {
//                    picker();
//                }

            }
        });


    }


    void picker(){
        new MaterialFilePicker()
                .withActivity(UploadStudyMaterialActivity.this)
                .withCloseMenu(true)
                .withHiddenFiles(true)
                .withFilter(Pattern.compile(".*\\.(pdf)$"))
                .withFilterDirectories(false)
                .withTitle("Album")
                .withRequestCode(REQUEST_CODE_PICKER)
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && null != data) {

            try {
                if (requestCode == REQUEST_CODE_PICKER) {
                    String p = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    File file = new File(p);
                    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("application/pdf"), file));
                    apiCallBack(getApiCommonController().uploadResourceAdmin(Common.DocType.STUDY_MATERIAL, null, filePart));
                    path.setText(p);

                }
            } catch (Exception e) {
                dialogFailed(getString(R.string.error_msg_something_went_wrong));
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Subscribe
    public void getResourceResponse(AddResourceResponse response) {
        if (Status.SUCCESS.value() == response.getStatus().value()) {
            url=response.getUrl();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void addSyllabusResponse(AddStudyResponse r) {
        if (Status.SUCCESS.value() == r.getStatus().value()) {
            dialogSuccessFinish(r.getMessage().getMessage());
        } else {
            dialogError(r.getMessage().getMessage());
        }
    }

}

