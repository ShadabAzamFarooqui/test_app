package com.app.mschooling.notice.activity;


import static com.app.mschooling.base.fragment.BaseFragment.REQUEST_CODE_PICKER;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.homework.adapter.UploadHomeWorkAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.progress_dialog.MyProgressDialogUploadMultiple;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.notice.AddNoticeRequest;
import com.mschooling.transaction.response.notice.AddNoticeResponse;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.MultimediaPicker;
import com.mschooling.multimediapicker.model.Image;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddNoticeActivity extends BaseActivity {

    EditText title;
    EditText notice;
    Button submit;
    RecyclerView recyclerView;
    Spinner role;
    LinearLayout insertImage;
    List<String> imageUrls;
    ArrayList<Uri> imageUriList = new ArrayList<>();
    ArrayList<Image> imageList = new ArrayList<>();
    String pdfUrl;
    int uploading;
    int size;
    public static final int IMAGE_SELECTION = 1;
    public static final int PDF_SELECTION = 2;
    int limit=3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_notice_fragment);

        toolbarClick(getString(R.string.add_notice));
        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.tittle);
        notice = findViewById(R.id.notice);
        submit = findViewById(R.id.submit);
        role = findViewById(R.id.role);
        insertImage = findViewById(R.id.insertImage);
        imageUrls = new ArrayList<>();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.please_enter_notice_title));
                    return;
                }
                if (notice.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.please_enter_notice));
                    return;
                }

                if (imageUriList.size() == 0) {
                    AddNoticeRequest addNoticeRequest = new AddNoticeRequest();
                    addNoticeRequest.setTitle(title.getText().toString());
                    addNoticeRequest.setContent(notice.getText().toString());
                    addNoticeRequest.setRole(Common.Role.valueOf(role.getSelectedItem().toString()));
                    List<Firebase> fireBaseList = new ArrayList<>();
                    addNoticeRequest.setFirebase(fireBaseList);
                    apiCallBack(getApiCommonController().postNotice(addNoticeRequest));
                } else {
                    size = imageUriList.size();
                    MyProgressDialogUploadMultiple.getInstance(AddNoticeActivity.this).show();
                    uploadImageUri("application/image", imageUriList.get(0).getPath());
                }

            }
        });

        insertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultimediaPicker.create(AddNoticeActivity.this)
                        .folderMode(true) // set folder mode (false by default)
                        .folderTitle(getString(R.string.album)) // folder selection title
                        .imageTitle(getString(R.string.tap_to_select)) // image selection title
                        .single() // single mode
                        .setOnlyImages(true)
                        .multi() // multi mode (default mode)
                        .limit(limit-imageList.size()) // max images can be selected (999 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                        .origin(imageList) // original selected images, used in multi mode
                        .start(REQUEST_CODE_PICKER);
            }
        });

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
                    insertImage.setVisibility(View.GONE);
                }
                for (int i = 0; i < imageList.size(); i++) {
                    imageUriList.add(Uri.parse(imageList.get(i).getPath()));
                }

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                recyclerView.setAdapter(new UploadHomeWorkAdapter(this, imageList,insertImage));


            }
        } catch (Exception e) {
            dialogFailed(getString(R.string.error_msg_something_went_wrong));
        }

    }

    @Subscribe
    public void notice(AddNoticeResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            title.setText("");
            notice.setText("");
            imageUriList.clear();
            pdfUrl = null;
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    private void uploadImageUri(String contentType, String  imagePath) {
        uploading++;
        Uri compressUri = compressImage(imagePath);
        File file = new File(compressUri.toString());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse(contentType), file));
        apiCallBackWithout(getApiCommonController().uploadResourceAdmin(Common.DocType.NOTICE, null, filePart));
        MyProgressDialogUploadMultiple.setCount("" + uploading, "" + size);
    }


    @Subscribe
    public void getResourceResponse(AddResourceResponse r) {
        if (Status.SUCCESS.value() == r.getStatus().value()) {
            imageUrls.add(r.getUrl());
            imageUriList.remove(0);
            if (imageUriList.size() > 0) {
                uploadImageUri("application/image", imageUriList.get(0).getPath());
            } else {
                AddNoticeRequest addNoticeRequest = new AddNoticeRequest();
                addNoticeRequest.setTitle(title.getText().toString());
                addNoticeRequest.setContent(notice.getText().toString());
                List<Firebase> fireBaseList = new ArrayList<>();
                for (int i = 0; i < imageUrls.size(); i++) {
                    Firebase firebase = new Firebase();
                    firebase.setUrl(imageUrls.get(i));
                    firebase.setFirebaseType(Common.FirebaseType.IMAGE);
                    fireBaseList.add(firebase);
                }
                if (pdfUrl != null) {
                    Firebase firebase = new Firebase();
                    firebase.setUrl(pdfUrl);
                    firebase.setFirebaseType(Common.FirebaseType.PDF);
                    firebase.setId(Helper.getRandom());
                    fireBaseList.add(firebase);
                }
                addNoticeRequest.setFirebase(fireBaseList);
                addNoticeRequest.setRole(Common.Role.valueOf(role.getSelectedItem().toString()));
                MyProgressDialogUploadMultiple.setDismiss();
                apiCallBack(getApiCommonController().postNotice(addNoticeRequest));
            }
        } else {
            dialogError(r.getMessage().getMessage());
        }
    }

}

