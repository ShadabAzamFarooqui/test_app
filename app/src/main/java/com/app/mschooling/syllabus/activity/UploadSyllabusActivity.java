package com.app.mschooling.syllabus.activity;

import static com.app.mschooling.base.fragment.BaseFragment.REQUEST_CODE_PICKER;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityUploadSyllabusBinding;
import com.app.mschooling.utils.MSchoolingUriRequestBody;
import com.app.mschooling.utils.PathUtil;
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

    ActivityResultLauncher<Intent> resultLauncher;

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

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);


        resultLauncher = registerForActivityResult(
                new ActivityResultContracts
                        .StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri sUri = data.getData();
                        System.out.println("http uri: " + sUri);
                        System.out.println("http path: " + sUri.getPath());
                        try {
                            String path = PathUtil.getPath(getApplicationContext(), sUri);
                            File file = new File(path);
                            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), new MSchoolingUriRequestBody(MediaType.parse("application/pdf"), getApplicationContext().getContentResolver(), sUri));
                            apiCallBack(getApiCommonController().uploadResourceAdmin(Common.DocType.SYLLABUS, null, filePart));
                            binding.path.setText(path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


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
                // check condition
                if (ActivityCompat.checkSelfPermission(
                        UploadSyllabusActivity.this,
                        Manifest.permission
                                .WRITE_EXTERNAL_STORAGE)
                        != PackageManager
                        .PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            UploadSyllabusActivity.this,
                            new String[]{
                                    Manifest.permission
                                            .WRITE_EXTERNAL_STORAGE},
                            1);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/pdf");
                    resultLauncher.launch(intent);
                }
            }
        });
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

