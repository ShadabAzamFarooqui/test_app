package com.app.mschooling.admin.fragment;


import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.app.mschooling.unused.ocr.activity.DigitalCameraActivity;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.admin.request.AdminIdentificationRequest;
import com.mschooling.transaction.common.admin.response.AdminIdentificationResponse;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.profile.UpdateAdminProfileRequest;
import com.mschooling.transaction.response.profile.GetAdminProfileResponse;
import com.mschooling.transaction.response.profile.UpdateStudentProfileResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminProfileIdentificationFragment extends BaseFragment {

    AdminIdentificationResponse adminIdentificationResponse;
    @BindView(R.id.aadhaarFront)
    ImageView aadhaarFront;
    @BindView(R.id.aadhaarBack)
    ImageView aadhaarBack;
    @BindView(R.id.uploadAadhaar)
    LinearLayout uploadAadhaar;

    @BindView(R.id.aadhaarNumber)
    EditText aadhaarNumber;

    @BindView(R.id.pan)
    ImageView pan;
    @BindView(R.id.uploadPan)
    LinearLayout uploadPan;
    @BindView(R.id.panNumber)
    EditText panNumber;

    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    @BindView(R.id.submit)
    Button submit;

    boolean update;

    GetAdminProfileResponse response;

    public AdminProfileIdentificationFragment(GetAdminProfileResponse response, boolean update) {
        if (response.getAdminIdentificationResponse() == null) {
            response.setAdminIdentificationResponse(new AdminIdentificationResponse());
        }
        this.update = update;
        this.response = response;
    }


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_profile_identification, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);

        adminIdentificationResponse = response.getAdminIdentificationResponse();
        if (adminIdentificationResponse == null) {
            adminIdentificationResponse = new AdminIdentificationResponse();
        }


        if (adminIdentificationResponse.getAdhaarFrontFirebase() != null) {
            aadhaarNumber.setText(adminIdentificationResponse.getAdhaarNo());

            Glide.with(this)
                    .load(adminIdentificationResponse.getAdhaarFrontFirebase().getUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(aadhaarFront);

            Glide.with(this)
                    .load(adminIdentificationResponse.getAdhaarBackFirebase().getUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(aadhaarBack);

        }


        if (adminIdentificationResponse.getPanFirebase() != null) {
            panNumber.setText(adminIdentificationResponse.getPanNo());
            Glide.with(this)
                    .load(adminIdentificationResponse.getPanFirebase().getUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(pan);


        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminIdentificationRequest identificationRequest = new AdminIdentificationRequest();
                identificationRequest.setAdhaarFrontFirebase(adminIdentificationResponse.getAdhaarFrontFirebase());
                identificationRequest.setAdhaarBackFirebase(adminIdentificationResponse.getAdhaarBackFirebase());
                identificationRequest.setPanFirebase(adminIdentificationResponse.getPanFirebase());
                identificationRequest.setAdhaarNo(aadhaarNumber.getText().toString());
                identificationRequest.setPanNo(panNumber.getText().toString());
                UpdateAdminProfileRequest request = new UpdateAdminProfileRequest();
                request.setAdminIdentificationRequest(identificationRequest);
                apiCallBack(getApiCommonController().updateAdminProfile(request));
            }
        });


        uploadAadhaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isNetworkAvailable(getActivity())) {
                    dialogNoInternet();
                    return;
                }
                Intent intent = new Intent(getActivity(), DigitalCameraActivity.class);
                intent.putExtra("type", "admin");
                intent.putExtra("docType", "AadhaarFront");
                startActivityForResult(intent, REQUEST_CODE_ADHAR);
            }
        });


        uploadPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isNetworkAvailable(getActivity())) {
                    dialogNoInternet();
                    return;
                }
                Intent intent = new Intent(getActivity(), DigitalCameraActivity.class);
                intent.putExtra("docType", "Pancard");
                intent.putExtra("type", "admin");
                startActivityForResult(intent, REQUEST_CODE_PAN);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }


    @Subscribe
    public void updateStudent(UpdateStudentProfileResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADHAR) {
                String urlFront = data.getStringExtra("urlFront");
                String urlBack = data.getStringExtra("urlBack");
                Firebase firebaseFront = new Firebase();
                Firebase firebaseBack = new Firebase();
                firebaseFront.setUrl(urlFront);
                firebaseBack.setUrl(urlBack);

                adminIdentificationResponse.setAdhaarFrontFirebase(firebaseFront);
                adminIdentificationResponse.setAdhaarBackFirebase(firebaseBack);
                Glide.with(this)
                        .load(adminIdentificationResponse.getAdhaarFrontFirebase().getUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(aadhaarFront);

                Glide.with(this)
                        .load(adminIdentificationResponse.getAdhaarBackFirebase().getUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(aadhaarBack);


            } else if (requestCode == REQUEST_CODE_PAN) {
                String url = data.getStringExtra("url");
                Firebase firebase = new Firebase();
                firebase.setUrl(url);
                adminIdentificationResponse.setPanFirebase(firebase);
                Glide.with(this)
                        .load(adminIdentificationResponse.getPanFirebase().getUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(pan);

            }

        }
    }
}

