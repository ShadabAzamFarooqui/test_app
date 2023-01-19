package com.app.mschooling.syllabus.activity;

import static com.app.mschooling.base.fragment.BaseFragment.REQUEST_CODE_PICKER;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityUploadSyllabusBinding;
//import com.mschooling.materialfilepicker.MaterialFilePicker;
//import com.mschooling.materialfilepicker.ui.FilePickerActivity;
import com.app.mschooling.study_material.activity.UploadStudyMaterialActivity;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.syllabus.AddSyllabusRequest;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.transaction.response.syllabus.AddSyllabusResponse;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadSyllabusActivity extends BaseActivity {

    ActivityUploadSyllabusBinding binding;

    String url;
    String syllabusId;
    String firebaseId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_syllabus);

        syllabusId = getIntent().getStringExtra("syllabusId");
        firebaseId = getIntent().getStringExtra("firebaseId");
        if (getIntent().getStringExtra("action") == null) {
            toolbarClick(getString(R.string.tool_upload_yllabus));
        } else {
            toolbarClick(getString(R.string.tool_update_yllabus));
            url = getIntent().getStringExtra("url");
            binding.name.setText(getIntent().getStringExtra("name"));
        }


        binding.submit.setOnClickListener(v -> {
            boolean isValid = true;
            if (url == null || url.trim().isEmpty()) {
                dialogError(getString(R.string.please_insert_pdf));
                isValid = setError(binding.imageLayout, binding.attachPdfText);
            }
            if (binding.name.getText().toString().trim().isEmpty()) {
                dialogError(getString(R.string.enter_name));
                isValid = setError(binding.nameLayout, binding.name, getString(R.string.enter_name));
            }
            if (isValid) {
                Firebase firebase = new Firebase();
                firebase.setName(binding.name.getText().toString());
                firebase.setId(firebaseId);
                firebase.setUrl(url);
                firebase.setFirebaseType(Common.FirebaseType.PDF);

                AddSyllabusRequest request = new AddSyllabusRequest();
                request.setId(syllabusId);
                request.getFirebases().add(firebase);
                request.setClassId(getIntent().getStringExtra("classId"));
                request.setSubjectId(getIntent().getStringExtra("subjectId"));
                apiCallBack(getApiCommonController().addSyllabus(request));
            } else {
                dialogError(getString(R.string.please_check_form));
            }
        });

        binding.imageLayout.setOnClickListener(new View.OnClickListener() {
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
                // Pass a source of context. Can be:
                //    .withActivity(Activity activity)
                //    .withFragment(Fragment fragment)
                //    .withSupportFragment(androidx.fragment.app.Fragment fragment)
                .withActivity(UploadSyllabusActivity.this)
                // With cross icon on the right side of toolbar for closing picker straight away
                .withCloseMenu(true)
                // Entry point path (user will start from it)
//                .withPath(alarmsFolder.absolutePath)
                // Root path (user won't be able to come higher than it)
                // Showing hidden files
                .withHiddenFiles(true)
                // Want to choose only jpg images
                .withFilter(Pattern.compile(".*\\.(pdf)$"))
                // Don't apply filter to directories names
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
                    String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    File file = new File(path);
                    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(file, MediaType.parse("application/pdf")));
                    apiCallBack(getApiCommonController().uploadResourceAdmin(Common.DocType.SYLLABUS, null, filePart));
                    binding.path.setText(path);

                }
            } catch (Exception e) {
                dialogFailed(getString(R.string.error_msg_something_went_wrong));
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Subscribe
    public void getResourceResponse(AddResourceResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            url = response.getUrl();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void addSyllabusResponse(AddSyllabusResponse r) {
        if (Status.SUCCESS.value().equals(r.getStatus().value())) {
            dialogSuccessFinish(r.getMessage().getMessage());
        } else {
            dialogError(r.getMessage().getMessage());
        }
    }

}

