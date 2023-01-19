package com.app.mschooling.unused;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.app.mschooling.add_teacher.AddTeacher4;
import com.app.mschooling.unused.ocr.activity.DigitalCameraActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.teacher.UpdateTeacherRequest;
import com.mschooling.transaction.response.teacher.UpdateTeacherResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTeacher3 extends BaseActivity {


    UpdateTeacherRequest mRequest = new UpdateTeacherRequest();;
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
    @BindView(R.id.skipLayout)
    LinearLayout skipLayout;
    @BindView(R.id.submit)
    Button submit;

    public static AddTeacher3 context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher3);
        ButterKnife.bind(this);
        context = this;
        toolbarClick(getString(R.string.tool_identification_detail));
        mRequest.getTeacherBasicRequest().setEnrollmentId(getIntent().getStringExtra("id"));

        uploadAadhaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isNetworkAvailable(getApplicationContext())) {
                    dialogError(getResources().getString(R.string.message_error_no_network));
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), DigitalCameraActivity.class);
                intent.putExtra("type", "teacher");
                intent.putExtra("docType", "AadhaarFront");
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivityForResult(intent, REQUEST_CODE_ADHAR);
            }
        });

        uploadPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isNetworkAvailable(getApplicationContext())) {
                    dialogError(getResources().getString(R.string.message_error_no_network));
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), DigitalCameraActivity.class);
                intent.putExtra("docType", "Pancard");
                intent.putExtra("type", "teacher");
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivityForResult(intent, REQUEST_CODE_PAN);
            }
        });

        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTeacher4.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiCallBack(getApiCommonController().updateTeacher(mRequest));
            }
        });

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
                        .load( mRequest.getTeacherIdentificationRequest().getAdhaarFrontFirebase().getUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(aadhaarFront);

                Glide.with(this)
                        .load( mRequest.getTeacherIdentificationRequest().getAdhaarBackFirebase().getUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(aadhaarBack);

            }else if (requestCode == REQUEST_CODE_PAN){
                Firebase firebasePan = new Firebase();
                firebasePan.setUrl(data.getStringExtra("url"));
                mRequest.getTeacherIdentificationRequest().setPanFirebase(firebasePan);
                Glide.with(this)
                        .load( mRequest.getTeacherIdentificationRequest().getPanFirebase().getUrl())
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
            Intent intent = new Intent(getApplicationContext(), AddTeacher4.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
            startActivity(intent);
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

}
