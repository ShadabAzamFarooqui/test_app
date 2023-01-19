package com.app.mschooling.gallery.activity;


import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.gallery.adadpter.UploadImageAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.progress_dialog.MyProgressDialogUploadMultiple;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.gallery.AddGalleryRequest;
import com.mschooling.transaction.response.gallery.AddGalleryResponse;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.model.Image;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageUploadActivity extends BaseActivity {

    LinearLayout mainLayout;
    ArrayList<Firebase> mUrlList;
    Button submit;
    RecyclerView recyclerView;
    UploadImageAdapter adapter;
    int uploading;
    int size;
    ArrayList<Image> imageList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image_upload);
        toolbarClick(getString(R.string.upload_image));
        mainLayout = findViewById(R.id.mainLayout);
        submit = findViewById(R.id.submit);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
        mUrlList = new ArrayList<>();
        imageList = new ArrayList<>();

         imageList = getIntent().getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

        setAdapter(imageList);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageList.size() > 0) {
                    uploading = 0;
                    size = imageList.size();
                    MyProgressDialogUploadMultiple.getInstance(ImageUploadActivity.this).show();
                    uploadImageUri(imageList.get(0).getPath());
                } else {
                    dialogError(getString(R.string.please_insert_image));
                }

            }
        });
    }


    void setAdapter(List<Image> uris) {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(ImageUploadActivity.this, 2));
        adapter = new UploadImageAdapter(ImageUploadActivity.this, uris);
        recyclerView.setAdapter(adapter);
    }


    @Subscribe
    public void getResourceResponse(AddResourceResponse r) {
        if (Status.SUCCESS.value() == r.getStatus().value()) {
            Firebase firebase=new Firebase();
            firebase.setUrl(r.getUrl());
            mUrlList.add(firebase);
            imageList.remove(0);
            if (imageList.size() > 0) {
                uploadImageUri(imageList.get(0).getPath());
            } else {
                AddGalleryRequest request=new AddGalleryRequest() ;
                request.setFirebases(mUrlList);
                apiCallBackWithout(getApiCommonController().addGallery(request));
            }
        } else {
            MyProgressDialogUploadMultiple.setDismiss();
            dialogError(r.getMessage().getMessage());
        }
    }

    @Subscribe
    public void addGalleryResponse(AddGalleryResponse r) {
        if (Status.SUCCESS.value() == r.getStatus().value()) {
            MyProgressDialogUploadMultiple.setDismiss();
            dialogSuccessFinish(r.getMessage().getMessage());
        } else {
            MyProgressDialogUploadMultiple.setDismiss();
            dialogError(r.getMessage().getMessage());
        }
    }



    private void uploadImageUri(String imagePath) {
        uploading++;
        Uri compressUri;
        try {
            compressUri = compressImage(imagePath);
        } catch (Exception e) {
            compressUri = Uri.parse(imagePath);
            e.printStackTrace();
        }

        File file = new File(compressUri.toString());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("application/image"), file));
        apiCallBackWithout(getApiCommonController().uploadResourceAdmin(Common.DocType.GALLERY, null, filePart));
        MyProgressDialogUploadMultiple.setCount("" + uploading, "" + size);
    }
}

