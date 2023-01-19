package com.app.mschooling.admin.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventToolbar;
import com.app.mschooling.utils.Preferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.admin.request.AdminProfileRequest;
import com.mschooling.transaction.common.admin.response.AdminProfileResponse;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.user.UserSetup;
import com.mschooling.transaction.request.profile.UpdateAdminProfileRequest;
import com.mschooling.transaction.response.configuration.ConfigurationResponse;
import com.mschooling.transaction.response.profile.GetAdminProfileResponse;
import com.mschooling.transaction.response.profile.UpdateAdminProfileResponse;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.MultimediaPicker;
import com.mschooling.multimediapicker.model.Image;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AdminProfileImageFragment extends BaseFragment {

    GetAdminProfileResponse response;

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
    boolean update;
    AdminProfileRequest adminProfileRequest;


    private ArrayList<Image> imageList = new ArrayList<>();

    public AdminProfileImageFragment(GetAdminProfileResponse response, boolean update) {
        this.response = response;
        this.update = update;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_profile_image, container, false);
        ButterKnife.bind(this, view);

        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        imageList = new ArrayList<>();
        if (response.getAdminProfileResponse() == null) {
            response.setAdminProfileResponse(new AdminProfileResponse());
        }

        if (!update) {
            submit.setVisibility(View.GONE);
        }

        try {
            if (response.getAdminProfileResponse().getProfileFirebase().getUrl() != null) {
                imageCapture.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);


                RequestOptions requestOptions = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                        .placeholder(R.drawable.default_image)
                        .priority(Priority.IMMEDIATE)
                        .encodeFormat(Bitmap.CompressFormat.PNG)
                        .format(DecodeFormat.DEFAULT);

                Glide.with(this)
                        .load(response.getAdminProfileResponse().getProfileFirebase().getUrl())
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .apply(requestOptions)
//                        .override(300, 500)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                shimmerFrameLayout.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                shimmerFrameLayout.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(image);

            } else {
                imageCapture.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            imageCapture.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.GONE);
        }

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MultimediaPicker.create(AdminProfileImageFragment.this)
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
                if (imageList.size() == 0) {
                    dialogError(getString(R.string.please_select_image));
                    return;
                }
                try {
                    multipart(imageList.get(0).getPath());
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }


            }
        });

        return view;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                imageList.clear();
                shimmerFrameLayout.setVisibility(View.GONE);
                imageCapture.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                if (requestCode == REQUEST_CODE_PICKER) {
                    imageList = data.getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                    if (submit.getVisibility() == View.GONE) {
                        multipart(imageList.get(0).getPath());
                    }
                    image.setImageURI(Uri.parse(imageList.get(0).getPath()));
                }
            }

        } catch (Exception ex) {
            Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    @Subscribe
    public void getResourceResponse(AddResourceResponse r) {
        if (Status.SUCCESS.value() == r.getStatus().value()) {
            Firebase firebase = new Firebase();
            firebase.setUrl(r.getUrl());
            adminProfileRequest = new AdminProfileRequest();
            adminProfileRequest.setProfileFirebase(firebase);
            UpdateAdminProfileRequest request = new UpdateAdminProfileRequest();
            request.setAdminProfileRequest(adminProfileRequest);
            apiCallBack(getApiCommonController().updateAdminProfile(request));

        } else {
            dialogError(r.getMessage().getMessage());
        }
    }


    @Subscribe
    public void uploadResponse(UpdateAdminProfileResponse updateStudentProfileResponse) {
        if (Status.SUCCESS.value() == updateStudentProfileResponse.getStatus().value()) {
            if (submit.getVisibility() == View.VISIBLE) {
                EventBus.getDefault().post(new EventToolbar("Personal"));
            }
            ConfigurationResponse configuration = Preferences.getInstance(getContext()).getConfiguration();
            UserSetup user=configuration.getUserSetup();
            user.setUrl(adminProfileRequest.getProfileFirebase().getUrl());
            Preferences.getInstance(getContext()).setConfiguration(configuration);
        } else {
            dialogError(updateStudentProfileResponse.getMessage().getMessage());
        }
    }


    void multipart(String path) {
        Uri uri = Uri.parse(path);
        Uri compressUri = Uri.parse(compressImage(uri));
        File file = new File(compressUri.toString());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse("application/image"), file));
        apiCallBack(getApiCommonController().uploadResource(Common.DocType.PROFILE, filePart));
    }

}
