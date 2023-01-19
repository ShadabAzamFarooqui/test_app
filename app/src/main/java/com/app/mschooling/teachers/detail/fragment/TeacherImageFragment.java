package com.app.mschooling.teachers.detail.fragment;


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
import com.app.mschooling.utils.Preferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.teacher.response.TeacherProfileResponse;
import com.mschooling.transaction.common.user.UserSetup;
import com.mschooling.transaction.request.teacher.UpdateTeacherRequest;
import com.mschooling.transaction.response.configuration.ConfigurationResponse;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.transaction.response.teacher.GetTeacherDetailResponse;
import com.mschooling.transaction.response.teacher.UpdateTeacherResponse;
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

public class TeacherImageFragment extends BaseFragment {

    TeacherProfileResponse response;
    String enrollmentId;
    String filePath;

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

    ArrayList<Image> imageList = new ArrayList<>();
    UpdateTeacherRequest mRequest = new UpdateTeacherRequest();


    public TeacherImageFragment(GetTeacherDetailResponse response) {
        this.response = response.getTeacherDetailResponse().getTeacherProfileResponse();
        this.enrollmentId = response.getTeacherDetailResponse().getTeacherBasicResponse().getEnrollmentId();
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_image, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, AppUser.getInstance().isUpdate());
        skipLayout.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();
        image.setVisibility(View.VISIBLE);
        imageCapture.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);

        if (response.getProfileFirebase() != null) {
            if (response.getProfileFirebase().getUrl() != null) {
                Glide.with(this)
                        .load(response.getProfileFirebase().getUrl())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
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
//                dialogProfile();
                MultimediaPicker.create(TeacherImageFragment.this)
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
                mRequest.getTeacherBasicRequest().setEnrollmentId(enrollmentId);
                apiCallBack(getApiCommonController().updateTeacher(mRequest));
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
                /*if (requestCode == REQUEST_CAMERA) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    fileUri = Uri.fromFile(Helper.getOutputMediaFile(MEDIA_TYPE_IMAGE, imageName));
                    Uri compressUri = Uri.parse(compressImage(this.fileUri));
                    filePath = compressUri.toString();
//                    filePath = FilePath.getPath(getActivity(), fileUri);
                    image.setImageURI(fileUri);
                    multipart();
                }
                if (requestCode == REQUEST_GALLERY) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Uri mImageUri = data.getData();
                    Cursor cursor = getActivity().getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    cursor.close();
                    Uri compressUri = Uri.parse(compressImage(mImageUri));
                    filePath = compressUri.toString();
//                    filePath = FilePath.getPath(getActivity(), mImageUri);
                    image.setImageURI(compressUri);
                    multipart();
                }*/

                if (resultCode == RESULT_OK) {
                    imageCapture.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                    if (requestCode == REQUEST_CODE_PICKER) {
                        imageList = data.getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                        image.setImageURI(Uri.parse(imageList.get(0).getPath()));
                        filePath = Uri.parse(compressImage(Uri.parse(imageList.get(0).getPath()))).toString();
                        multipart();
                    }
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }


    @Subscribe
    public void updateTeacher(UpdateTeacherResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {

            ConfigurationResponse configuration = Preferences.getInstance(getContext()).getConfiguration();
            UserSetup user = configuration.getUserSetup();
            user.setUrl(mRequest.getTeacherProfileRequest().getProfileFirebase().getUrl());
            Preferences.getInstance(getContext()).setConfiguration(configuration);
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
            mRequest.getTeacherProfileRequest().setProfileFirebase(firebase);

        } else {
            dialogError(r.getMessage().getMessage());
        }
    }


    void multipart() {
        File file = new File(filePath);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse("application/image"), file));
        apiCallBack(getApiCommonController().uploadResourceAdmin(Common.DocType.PROFILE, enrollmentId, filePart));
    }


}

