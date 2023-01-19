package com.app.mschooling.teachers.profile.fragment;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventToolbar;
import com.app.mschooling.utils.Helper;
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
import com.mschooling.transaction.common.teacher.request.TeacherProfileRequest;
import com.mschooling.transaction.common.teacher.response.TeacherProfileResponse;
import com.mschooling.transaction.request.profile.UpdateTeacherProfileRequest;
import com.mschooling.transaction.response.profile.GetTeacherProfileResponse;
import com.mschooling.transaction.response.profile.UpdateTeacherProfileResponse;
import com.mschooling.transaction.response.resource.AddResourceResponse;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.GridHolder;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.MultimediaPicker;
import com.mschooling.multimediapicker.model.Image;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class TeacherProfileImageFragment extends BaseFragment {

    GetTeacherProfileResponse response;

    @BindView(R.id.captureImage)
    RelativeLayout captureImage;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.imageCapture)
    ImageView imageCapture;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.skipLayout)
    LinearLayout skipLayout;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;
    @BindView(R.id.shimmerFrameLayout)
    ShimmerFrameLayout shimmerFrameLayout;
    boolean update;
    String filePath;
    public Uri fileUri;
    public String imageName;
    public static final int REQUEST_GALLERY = 11;
    public static final int REQUEST_CAMERA = 22;
    ArrayList<Image> imageList=new ArrayList<>();

    public TeacherProfileImageFragment(GetTeacherProfileResponse response, boolean update) {
        this.response = response;
        this.update = update;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_image, container, false);
        ButterKnife.bind(this, view);
//        Helper.enableDisableView(mainLayout, submit, update);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);

        if (response.getTeacherProfileResponse() == null) {
            response.setTeacherProfileResponse(new TeacherProfileResponse());
        }

        if (!update) {
            submit.setVisibility(View.GONE);
            skipLayout.setVisibility(View.GONE);
        }

        try {
            if (response.getTeacherProfileResponse().getProfileFirebase().getUrl() != null) {
                imageCapture.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(response.getTeacherProfileResponse().getProfileFirebase().getUrl())
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
//                dialogProfile();

                MultimediaPicker.create(TeacherProfileImageFragment.this)
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
                if (filePath == null) {
                    dialogError(getString(R.string.please_select_image));
                    return;
                }
                multipart();

            }
        });

        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSwitching(new TeacherProfileAddressFragment(response, update));
                EventBus.getDefault().post(new EventToolbar("Personal"));
            }
        });

        return view;

    }


    DialogPlus dialog = null;

    public void dialogProfile() {

        DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(getContext());
        dialogPlusBuilder.setHeader(R.layout.dialog_profile);
        dialogPlusBuilder.setContentHolder(new GridHolder(1));
        dialogPlusBuilder.setGravity(Gravity.BOTTOM);
        dialogPlusBuilder.setCancelable(true);

        dialogPlusBuilder.setMargin(5, 0, 5, 10);
        dialogPlusBuilder.setPadding(0, 0, 0, 10);

//        dialogPlusBuilder.setFooter(R.layout.footer);
        dialogPlusBuilder.setExpanded(false); // This will enable the expand feature, (similar to android L share dialog)
        dialog = dialogPlusBuilder.create();
        TextView tittle = (TextView) dialog.findViewById(R.id.tittle);
        tittle.setText(getString(R.string.profile));
        LinearLayout close = (LinearLayout) dialog.findViewById(R.id.close);
        LinearLayout camera = (LinearLayout) dialog.findViewById(R.id.camera);
        LinearLayout camera1 = (LinearLayout) dialog.findViewById(R.id.camera1);
        ImageView camera2 = (ImageView) dialog.findViewById(R.id.camera2);
        LinearLayout gallery = (LinearLayout) dialog.findViewById(R.id.gallery);
        LinearLayout gallery1 = (LinearLayout) dialog.findViewById(R.id.gallery1);
        ImageView gallery2 = (ImageView) dialog.findViewById(R.id.gallery2);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera();
            }
        });
        camera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera();
            }
        });
        camera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery();
            }
        });

        gallery1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery();
            }
        });
        gallery2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    void camera() {
        dialog.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageName = "PRO" + System.currentTimeMillis();
        if (Build.VERSION.SDK_INT > 23) {
            fileUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", Helper.getOutputMediaFile(MEDIA_TYPE_IMAGE, imageName));
        } else {
            fileUri = Uri.fromFile(Helper.getOutputMediaFile(MEDIA_TYPE_IMAGE, imageName));
        }
        //   this line is to be used for android 9
        List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getActivity().grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.putExtra("output", fileUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    void gallery() {
        /*imageName = "PRO" + System.currentTimeMillis();
        dialog.dismiss();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);*/
        imageName = "PRO" + System.currentTimeMillis();
        dialog.dismiss();
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, REQUEST_GALLERY);
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

                    if (submit.getVisibility() == View.GONE) {
                        multipart();
                    }
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
                    if (submit.getVisibility() == View.GONE) {
                        multipart();
                    }
                }*/


                if (resultCode == RESULT_OK) {
                    imageCapture.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                    if (requestCode == REQUEST_CODE_PICKER) {
                        imageList = data.getParcelableArrayListExtra(GalleryPickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                        image.setImageURI(Uri.parse(imageList.get(0).getPath()));
                        filePath = Uri.parse(compressImage(Uri.parse(imageList.get(0).getPath()))).toString();
                        if (submit.getVisibility() == View.GONE) {
                            multipart();
                        }
                    }
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
            Firebase firebase=new Firebase();
            firebase.setUrl(r.getUrl());
            TeacherProfileRequest teacherProfileRequest = new TeacherProfileRequest();
            teacherProfileRequest.setProfileFirebase(firebase);
            UpdateTeacherProfileRequest request = new UpdateTeacherProfileRequest();
            request.setTeacherProfileRequest(teacherProfileRequest);
            apiCallBack(getApiCommonController().updateTeacherProfile(request));

        } else {
            dialogError(r.getMessage().getMessage());
        }
    }


    @Subscribe
    public void uploadResponse(UpdateTeacherProfileResponse updateTeacherProfileResponse) {
        if (Status.SUCCESS.value() == updateTeacherProfileResponse.getStatus().value()) {
            if (submit.getVisibility() == View.VISIBLE) {
                fragmentSwitching(new TeacherProfileAddressFragment(response, update));
                EventBus.getDefault().post(new EventToolbar("Personal"));
            }
        } else {
            dialogError(updateTeacherProfileResponse.getMessage().getMessage());
        }
    }


    void multipart() {
        File file = new File(filePath);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse("application/image"), file));
        apiCallBack(getApiCommonController().uploadResource(Common.DocType.PROFILE, filePart));
    }

}
