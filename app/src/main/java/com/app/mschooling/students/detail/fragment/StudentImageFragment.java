package com.app.mschooling.students.detail.fragment;


import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.student.response.StudentProfileResponse;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.transaction.response.student.GetStudentDetailResponse;
import com.mschooling.transaction.response.student.UpdateStudentResponse;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.MultimediaPicker;
import com.mschooling.multimediapicker.model.Image;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class StudentImageFragment extends BaseFragment {

    StudentProfileResponse response;
    String enrollmentId;

    @BindView(R.id.captureImage)
    RelativeLayout captureImage;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.imageCapture)
    ImageView imageCapture;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.shimmerFrameLayout)
    ShimmerFrameLayout shimmerFrameLayout;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;
    @BindView(R.id.skipLayout)
    LinearLayout skipLayout;

    private ArrayList<Image> imageList = new ArrayList<>();
    UpdateStudentRequest mRequest = new UpdateStudentRequest();
    boolean update;

    public StudentImageFragment(GetStudentDetailResponse response,boolean update) {
        this.response = response.getStudentDetailResponse().getStudentProfileResponse();
        enrollmentId=response.getStudentDetailResponse().getStudentBasicResponse().getEnrollmentId();
        this.update = update;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_image, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);
        skipLayout.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();
        image.setVisibility(View.VISIBLE);
        imageCapture.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.GONE);
        imageList = new ArrayList<>();
        if (response.getProfileFirebase() != null) {
            if (response.getProfileFirebase().getUrl() != null) {
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(response.getProfileFirebase().getUrl())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .transition(DrawableTransitionOptions.withCrossFade())
//                        .apply(new RequestOptions().placeholder(R.drawable.default_image))
                        .skipMemoryCache(true)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                image.setVisibility(View.VISIBLE);
                                imageCapture.setVisibility(View.GONE);
                                shimmerFrameLayout.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                image.setVisibility(View.VISIBLE);
                                imageCapture.setVisibility(View.GONE);
                                shimmerFrameLayout.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(image);
            } else {
                imageCapture.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        } else {
            imageCapture.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.GONE);
        }

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MultimediaPicker.create(StudentImageFragment.this)
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
                mRequest.getStudentBasicRequest().setEnrollmentId(enrollmentId);
                apiCallBack(getApiCommonController().updateStudent(mRequest));
            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                shimmerFrameLayout.setVisibility(View.GONE);
                imageCapture.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                imageList.clear();
                if (requestCode == REQUEST_CODE_PICKER) {
                    imageList = data.getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                    multipart(imageList.get(0).getPath());
                    image.setImageURI(Uri.parse(imageList.get(0).getPath()));
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }


    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void getResourceResponse(AddResourceResponse r) {
        if (Status.SUCCESS.value() == r.getStatus().value()) {
            Firebase firebase = new Firebase();
            firebase.setUrl(r.getUrl());
            mRequest.getStudentProfileRequest().setProfileFirebase(firebase);

        } else {
            dialogError(r.getMessage().getMessage());
        }
    }


    void multipart(String path) {
        Uri uri = Uri.parse(path);
        Uri compressUri = Uri.parse(compressImage(uri));
        File file = new File(compressUri.toString());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse("application/image"), file));
        apiCallBack(getApiCommonController().uploadResourceAdmin(Common.DocType.PROFILE, enrollmentId, filePart));
    }


}

