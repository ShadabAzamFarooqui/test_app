package com.app.mschooling.teachers.detail.fragment;

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
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.teacher.response.TeacherIdentificationResponse;
import com.mschooling.transaction.request.teacher.UpdateTeacherRequest;
import com.mschooling.transaction.response.teacher.GetTeacherDetailResponse;
import com.mschooling.transaction.response.teacher.UpdateTeacherResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeacherIdentificationFragment extends BaseFragment {


    UpdateTeacherRequest mRequest = new UpdateTeacherRequest();
    TeacherIdentificationResponse response;
    String enrollmentId;

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
    @BindView(R.id.skipLayout)
    LinearLayout skipLayout;

    public TeacherIdentificationFragment(GetTeacherDetailResponse response) {
        this.response = response.getTeacherDetailResponse().getTeacherIdentificationResponse();
        this.enrollmentId = response.getTeacherDetailResponse().getTeacherBasicResponse().getEnrollmentId();
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identification_teacher, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, AppUser.getInstance().isUpdate());
        skipLayout.setVisibility(View.GONE);
        mRequest.getTeacherBasicRequest().setEnrollmentId(enrollmentId);
        mRequest.getTeacherIdentificationRequest().setAdhaarFrontFirebase(response.getAdhaarFrontFirebase());
        mRequest.getTeacherIdentificationRequest().setAdhaarBackFirebase(response.getAdhaarBackFirebase());
        mRequest.getTeacherIdentificationRequest().setPanFirebase(response.getPanFirebase());


        if (mRequest.getTeacherIdentificationRequest().getAdhaarFrontFirebase() != null) {
            aadhaarNumber.setText(mRequest.getTeacherIdentificationRequest().getAdhaarNo());

            Glide.with(this)
                    .load(mRequest.getTeacherIdentificationRequest().getAdhaarFrontFirebase().getUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(aadhaarFront);

            Glide.with(this)
                    .load(mRequest.getTeacherIdentificationRequest().getAdhaarBackFirebase().getUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(aadhaarBack);

        }

        if (mRequest.getTeacherIdentificationRequest().getPanFirebase() != null) {
            aadhaarNumber.setText(mRequest.getTeacherIdentificationRequest().getAdhaarNo());

            Glide.with(this)
                    .load(mRequest.getTeacherIdentificationRequest().getPanFirebase().getUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(pan);


        }




        uploadAadhaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isNetworkAvailable(getContext())) {
                    dialogNoInternet();
                    return;
                }

                Intent intent = new Intent(getContext(), DigitalCameraActivity.class);
                intent.putExtra("docType", "AadhaarFront");
                intent.putExtra("type", "teacher");
                intent.putExtra("id", enrollmentId);
                startActivityForResult(intent, REQUEST_CODE_ADHAR);
            }
        });

        uploadPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isNetworkAvailable(getContext())) {
                    dialogNoInternet();
                    return;
                }
                Intent intent = new Intent(getContext(), DigitalCameraActivity.class);
                intent.putExtra("docType", "Pancard");
                intent.putExtra("type", "teacher");
                intent.putExtra("id", enrollmentId);
                startActivityForResult(intent, REQUEST_CODE_PAN);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequest.getTeacherIdentificationRequest().setAdhaarNo(aadhaarNumber.getText().toString());
                mRequest.getTeacherIdentificationRequest().setPanNo(aadhaarNumber.getText().toString());
                apiCallBack(getApiCommonController().updateTeacher(mRequest));
            }
        });


        return view;
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
                mRequest.getTeacherIdentificationRequest().setAdhaarFrontFirebase(firebaseFront);
                mRequest.getTeacherIdentificationRequest().setAdhaarBackFirebase(firebaseBack);
                Glide.with(this)
                        .load(mRequest.getTeacherIdentificationRequest().getAdhaarFrontFirebase().getUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(aadhaarFront);

                Glide.with(this)
                        .load(mRequest.getTeacherIdentificationRequest().getAdhaarBackFirebase().getUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(aadhaarBack);

            } else if (requestCode == REQUEST_CODE_PAN) {
                Firebase firebasePan = new Firebase();
                firebasePan.setUrl(data.getStringExtra("url"));
                mRequest.getTeacherIdentificationRequest().setPanFirebase(firebasePan);
                Glide.with(this)
                        .load(mRequest.getTeacherIdentificationRequest().getPanFirebase().getUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(pan);
            }

        }

    }

    @Subscribe
    public void updateTeacher(UpdateTeacherResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

}
