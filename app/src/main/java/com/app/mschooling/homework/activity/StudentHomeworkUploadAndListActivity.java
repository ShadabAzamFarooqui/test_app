package com.app.mschooling.homework.activity;

import static com.app.mschooling.base.fragment.BaseFragment.REQUEST_CODE_PICKER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityStudentHomeworkUploadAndListBinding;
import com.app.mschooling.com.databinding.ActivityUploadSyllabusBinding;
import com.app.mschooling.event_handler.EventBasedOnType;
import com.app.mschooling.homework.adapter.HomeworkStudentSubmittedAdapter;
import com.app.mschooling.homework.adapter.UploadHomeWorkAdapter;
import com.app.mschooling.notice.activity.AddNoticeActivity;
import com.app.mschooling.other.adapter.ImageListAdapter;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.progress_dialog.MyProgressDialogUploadMultiple;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.MultimediaPicker;
import com.mschooling.multimediapicker.model.Image;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.homework.AddStudentWorksheetRequest;
import com.mschooling.transaction.request.homework.UpdateStudentWorksheetRequest;
import com.mschooling.transaction.request.notice.AddNoticeRequest;
import com.mschooling.transaction.response.homework.AddStudentWorksheetResponse;
import com.mschooling.transaction.response.homework.GetStudentUploadedWorksheetResponse;
import com.mschooling.transaction.response.homework.UpdateStudentWorksheetResponse;
import com.mschooling.transaction.response.resource.AddResourceResponse;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class StudentHomeworkUploadAndListActivity extends BaseActivity {

    ActivityStudentHomeworkUploadAndListBinding binding;

    int size;
    int limit = 9;
    int uploading;
    List<String> imageUrls = new ArrayList<>();
    ArrayList<Image> imageList = new ArrayList<>();
    UploadHomeWorkAdapter adapter;

    String homeworkId,enrollmentId;

    GetStudentUploadedWorksheetResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_student_homework_upload_and_list);
        toolbarClick(getString(R.string.homework));
        enrollmentId=getIntent("enrollmentId");
        homeworkId=getIntent("homeworkId");
        getHomeworkApi();
        binding.listLayout.setVisibility(View.GONE);
        binding.uploadLayout.setVisibility(View.GONE);

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.listLayout.setVisibility(View.GONE);
                binding.uploadLayout.setVisibility(View.VISIBLE);
            }
        });
        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.listLayout.setVisibility(View.VISIBLE);
                binding.uploadLayout.setVisibility(View.GONE);
            }
        });

        binding.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionLayoutClick(binding.accept.getText().toString(), Common.WorksheetStatus.APPROVED);
            }
        });


        binding.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionLayoutClick(binding.reject.getText().toString(), Common.WorksheetStatus.REJECTED);
            }
        });

        binding.insertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultimediaPicker.create(StudentHomeworkUploadAndListActivity.this)
                        .folderMode(true) // set folder mode (false by default)
                        .folderTitle(getString(R.string.album)) // folder selection title
                        .imageTitle(getString(R.string.tap_to_select)) // image selection title
                        .single() // single mode
                        .setOnlyImages(true)
                        .multi() // multi mode (default mode)
                        .limit(limit - imageList.size()) // max images can be selected (999 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                        .origin(imageList) // original selected images, used in multi mode
                        .start(REQUEST_CODE_PICKER);
            }
        });


        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                size = imageList.size();
                if (size == 0) {
                    dialogError(getString(R.string.please_insert_image));
                    return;
                }
                MyProgressDialogUploadMultiple.getInstance(StudentHomeworkUploadAndListActivity.this).show();
                uploadImageUri("application/image",
                        imageList.get(0).getPath());
            }
        });

    }


    void getHomeworkApi() {
        apiCallBack(getApiCommonController().getSubmittedHomework(ParameterConstant.getRole(this), homeworkId, enrollmentId));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
                imageList.addAll(data.getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES));
                if (imageList.size() > limit) {
                    imageList.subList(limit, imageList.size()).clear();
                }
                if (imageList.size() == limit) {
                    binding.insertImage.setVisibility(View.GONE);
                }


                binding.recyclerViewUpload.setVisibility(View.VISIBLE);
                binding.recyclerViewUpload.setHasFixedSize(true);
                binding.recyclerViewUpload.setFocusable(false);
                binding.recyclerViewUpload.setNestedScrollingEnabled(false);
                binding.recyclerViewUpload.setLayoutManager(new GridLayoutManager(this, 3));
                adapter = new UploadHomeWorkAdapter(this, imageList, binding.insertImage);
                binding.recyclerViewUpload.setAdapter(adapter);

            }
        } catch (Exception e) {
            dialogFailed(getString(R.string.error_msg_something_went_wrong));
        }

    }

    private void uploadImageUri(String contentType, String imagePath) {
        uploading++;
        File file = new File(compressImage(imagePath).toString());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse(contentType), file));
        apiCallBackWithout(getApiCommonController().uploadResourceAdmin(Common.DocType.STUDENT_HOMEWORK, null, filePart));
        MyProgressDialogUploadMultiple.setCount("" + uploading, "" + size);
    }


    @Subscribe
    public void getResourceResponse(AddResourceResponse r) {
        if (Status.SUCCESS.value() == r.getStatus().value()) {
            imageUrls.add(r.getUrl());
            imageList.remove(0);
            if (imageList.size() > 0) {
                uploadImageUri("application/image", imageList.get(0).getPath());
            } else {
                MyProgressDialogUploadMultiple.setDismiss();
                List<Firebase> fireBaseList = new ArrayList<>();
                for (int i = 0; i < imageUrls.size(); i++) {
                    Firebase firebase = new Firebase();
                    firebase.setUrl(imageUrls.get(i));
                    firebase.setFirebaseType(Common.FirebaseType.IMAGE);
                    fireBaseList.add(firebase);
                }

                AddStudentWorksheetRequest request = new AddStudentWorksheetRequest();
                request.setFirebases(fireBaseList);
                request.setId(homeworkId);
                apiCallBack(getApiCommonController().uploadWorksheet(ParameterConstant.getRole(this),request));

            }
        } else {
            dialogError(r.getMessage().getMessage());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    public void addHomeWorkResponse(AddStudentWorksheetResponse response) {
        imageList.clear();
        adapter.notifyDataSetChanged();
        if (Status.SUCCESS.value() == response.getStatus().value()) {
            binding.listLayout.setVisibility(View.VISIBLE);
            binding.uploadLayout.setVisibility(View.GONE);
            getHomeworkApi();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void getHomework(GetStudentUploadedWorksheetResponse response) {
        if (Status.SUCCESS.value() == response.getStatus().value()) {
            this.response=response;
            if (response.getStudentUploadedWorksheetResponses().size() > 0) {
                binding.listLayout.setVisibility(View.VISIBLE);
                binding.uploadLayout.setVisibility(View.GONE);
                binding.status.setText(response.getWorksheetStatus().value());
                if (Preferences.isStudent(this)) {
                    binding.actionLayout.setVisibility(View.GONE);
                    if (response.getWorksheetStatus().value().equals(Common.WorksheetStatus.APPROVED.value())) {
                        binding.submit.setVisibility(View.GONE);
                        binding.status.setVisibility(View.VISIBLE);
                    } else if (response.getWorksheetStatus().value().equals(Common.WorksheetStatus.REJECTED.value())) {
                        binding.submit.setVisibility(View.VISIBLE);
                        binding.status.setVisibility(View.VISIBLE);
                    } else {
                        binding.submit.setVisibility(View.VISIBLE);
                        binding.status.setVisibility(View.GONE);
                    }
                } else {
                    binding.submit.setVisibility(View.GONE);

                    if (response.getWorksheetStatus().value().equals(Common.WorksheetStatus.APPROVED.value())) {
                        binding.actionLayout.setVisibility(View.GONE);
                        binding.status.setVisibility(View.VISIBLE);
                    } else {
                        binding.actionLayout.setVisibility(View.VISIBLE);
                        binding.status.setVisibility(View.GONE);
                    }
                }

                binding.recyclerView.setHasFixedSize(true);
                binding.recyclerView.setFocusable(false);
                binding.recyclerView.setNestedScrollingEnabled(false);
                binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
                binding.recyclerView.setAdapter(new HomeworkStudentSubmittedAdapter(this, response.getStudentUploadedWorksheetResponses()));

            } else {
                binding.listLayout.setVisibility(View.GONE);
                binding.uploadLayout.setVisibility(View.VISIBLE);
            }

        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @SuppressLint("SetTextI18n")
    void actionLayoutClick(String actionText, Common.WorksheetStatus status) {
        String text = Helper.capsFirstOtherInLower(actionText);
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);
        TextView tittle = dialog.findViewById(R.id.tittle);
        TextView message = dialog.findViewById(R.id.message);
        TextView action = dialog.findViewById(R.id.action);
        TextView cancel = dialog.findViewById(R.id.cancel);

        message.setText(getString(R.string.are_you_sure_you_want_to) + " " + text.toLowerCase() + "?");
        tittle.setText(text);
        action.setText(text);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateStudentWorksheetRequest request = new UpdateStudentWorksheetRequest();
                request.setEnrollmentId(enrollmentId);
                request.setWorksheetStatus(status);
                request.setId(homeworkId);
                apiCallBack(getApiCommonController().updateWorkSheetStatus(ParameterConstant.getRole(getApplicationContext()), request));
                dialog.dismiss();
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


    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Subscribe
    public void updateResponse(UpdateStudentWorksheetResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            getHomeworkApi();
        } else {
            dialogError(response.getMessage().getMessage());
        }

    }

}