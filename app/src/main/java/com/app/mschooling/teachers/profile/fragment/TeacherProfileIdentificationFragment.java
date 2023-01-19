package com.app.mschooling.teachers.profile.fragment;

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
import com.app.mschooling.event_handler.EventToolbar;
import com.app.mschooling.utils.Helper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.teacher.request.TeacherIdentificationRequest;
import com.mschooling.transaction.common.teacher.response.TeacherAddressResponse;
import com.mschooling.transaction.common.teacher.response.TeacherIdentificationResponse;
import com.mschooling.transaction.request.profile.UpdateTeacherProfileRequest;
import com.mschooling.transaction.response.profile.GetTeacherProfileResponse;
import com.mschooling.transaction.response.profile.UpdateTeacherProfileResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeacherProfileIdentificationFragment extends BaseFragment {


    TeacherIdentificationRequest mRequest = new TeacherIdentificationRequest();
    GetTeacherProfileResponse response;
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
    @BindView(R.id.skipLayout)
    LinearLayout skipLayout;
    @BindView(R.id.submit)
    Button submit;

    boolean update;


    public TeacherProfileIdentificationFragment(GetTeacherProfileResponse response, boolean update) {
        if (response.getTeacherAddressResponse() == null) {
            response.setTeacherAddressResponse(new TeacherAddressResponse());
        }
        this.update = update;
        this.response = response;
    }



    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identification_teacher, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);
        if (!update){
            skipLayout.setVisibility(View.GONE);
        }
        if (response.getTeacherIdentificationResponse()==null){
            response.setTeacherIdentificationResponse(new TeacherIdentificationResponse());
        }
        TeacherIdentificationResponse teacherIdentificationResponse=response.getTeacherIdentificationResponse();
        mRequest.setAdhaarFrontFirebase(teacherIdentificationResponse.getAdhaarFrontFirebase());
        mRequest.setAdhaarBackFirebase(teacherIdentificationResponse.getAdhaarBackFirebase());
        mRequest.setPanFirebase(teacherIdentificationResponse.getPanFirebase());


        if (mRequest.getAdhaarFrontFirebase() != null) {
            aadhaarNumber.setText(mRequest.getAdhaarNo());

            Glide.with(this)
                    .load(mRequest.getAdhaarFrontFirebase().getUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(aadhaarFront);

            Glide.with(this)
                    .load(mRequest.getAdhaarBackFirebase().getUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(aadhaarBack);

        }

        if (mRequest.getPanFirebase() != null) {
            aadhaarNumber.setText(mRequest.getAdhaarNo());

            Glide.with(this)
                    .load(mRequest.getPanFirebase().getUrl())
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
                startActivityForResult(intent, REQUEST_CODE_PAN);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateTeacherProfileRequest request=new UpdateTeacherProfileRequest();
                request.setTeacherIdentificationRequest(mRequest);
                apiCallBack(getApiCommonController().updateTeacherProfile(request));
            }
        });

        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSwitching(new TeacherProfileBankDetailFragment(response, update));
                EventBus.getDefault().post(new EventToolbar("Bank"));
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
                mRequest.setAdhaarFrontFirebase(firebaseFront);
                mRequest.setAdhaarBackFirebase(firebaseBack);
                Glide.with(this)
                        .load(mRequest.getAdhaarFrontFirebase().getUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(aadhaarFront);

                Glide.with(this)
                        .load(mRequest.getAdhaarBackFirebase().getUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(aadhaarBack);

            } else if (requestCode == REQUEST_CODE_PAN) {
                Firebase firebasePan = new Firebase();
                firebasePan.setUrl(data.getStringExtra("url"));
                mRequest.setPanFirebase(firebasePan);
                Glide.with(this)
                        .load(mRequest.getPanFirebase().getUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(pan);
            }

        }

    }

    @Subscribe
    public void updateTeacher(UpdateTeacherProfileResponse response1) {
        if (response1.getStatus().value() == Status.SUCCESS.value()) {
            fragmentSwitching(new TeacherProfileBankDetailFragment(response, update));
            EventBus.getDefault().post(new EventToolbar("Bank"));
        } else {
            dialogFailed(response1.getMessage().getMessage());
        }
    }

}
