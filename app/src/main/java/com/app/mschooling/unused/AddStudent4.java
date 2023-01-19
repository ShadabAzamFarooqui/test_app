package com.app.mschooling.unused;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.app.mschooling.add_student.AddStudent5;
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
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.student.UpdateStudentResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;





public class AddStudent4 extends BaseActivity {


    UpdateStudentRequest mRequest;

    @BindView(R.id.aadhaarFront)
    ImageView aadhaarFront;
    @BindView(R.id.aadhaarBack)
    ImageView aadhaarBack;
    @BindView(R.id.uploadAadhaar)
    LinearLayout uploadAadhaar;
    @BindView(R.id.aadhaarNumber)
    EditText aadhaarNumber;
    @BindView(R.id.submit)
    Button submit;





    @BindView(R.id.aadhaarFrontFather)
    ImageView aadhaarFrontFather;
    @BindView(R.id.aadhaarBackFather)
    ImageView aadhaarBackFather;
    @BindView(R.id.uploadAadhaarFather)
    LinearLayout uploadAadhaarFather;
    @BindView(R.id.fatherAadhaarNumber)
    EditText fatherAadhaarNumber;

    @BindView(R.id.fatherPanNumber)
    EditText fatherPanNumber;
    @BindView(R.id.skipLayout)
    LinearLayout skipLayout;

    public static AddStudent4 context;

    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_student4);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.tool_identification));
        context = this;
        mRequest = new UpdateStudentRequest();
        mRequest.getStudentBasicRequest().setEnrollmentId(getIntent().getStringExtra("id"));
        mRequest.getStudentBasicRequest().setMobile(getIntent().getStringExtra("mobile"));

        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), AddStudent5.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequest.getStudentIdentificationRequest().setAdhaarNo(aadhaarNumber.getText().toString());
                mRequest.getStudentIdentificationRequest().setAdhaarNo(aadhaarNumber.getText().toString());
                mRequest.getStudentIdentificationRequest().setfAdhaarNo(fatherAadhaarNumber.getText().toString());
                mRequest.getStudentIdentificationRequest().setfPanNo(fatherPanNumber.getText().toString());
                apiCallBack(getApiCommonController().updateStudent(mRequest));            }
        });


        uploadAadhaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isNetworkAvailable(getApplicationContext())) {
                    dialogError(getResources().getString(R.string.message_error_no_network));
                    return;
                }
                type="student";
                Intent intent = new Intent(getApplicationContext(), DigitalCameraActivity.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                intent.putExtra("type", type);
                intent.putExtra("docType", "AadhaarFront");
                startActivityForResult(intent, REQUEST_CODE_ADHAR);
            }
        });


        uploadAadhaarFather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isNetworkAvailable(getApplicationContext())) {
                    dialogError(getResources().getString(R.string.message_error_no_network));
                    return;
                }
                type="father";
                Intent intent = new Intent(getApplicationContext(), DigitalCameraActivity.class);
                intent.putExtra("docType", "AadhaarFront");
                intent.putExtra("id", getIntent().getStringExtra("id"));
                intent.putExtra("type", type);
                startActivityForResult(intent, REQUEST_CODE_ADHAR);


                /* Intent intent = new Intent(AddStudent4.this, DigitalCameraActivity.class);
                intent.putExtra("docType", "Pancard");
                intent.putExtra("type", "father");
                startActivityForResult(intent, REQUEST_CODE_PAN);*/
            }
        });
    }

    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            Intent intent = new Intent(getApplicationContext(), AddStudent5.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
            startActivity(intent);
        } else {
            dialogError(response.getMessage().getMessage());
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
                if (type.equals("student")) {
                    mRequest.getStudentIdentificationRequest().setAdhaarFrontFirebase(firebaseFront);
                    mRequest.getStudentIdentificationRequest().setAdhaarBackFirebase(firebaseBack);
                    Glide.with(this)
                            .load(mRequest.getStudentIdentificationRequest().getAdhaarFrontFirebase().getUrl())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into(aadhaarFront);

                    Glide.with(this)
                            .load(mRequest.getStudentIdentificationRequest().getAdhaarBackFirebase().getUrl())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into(aadhaarBack);
                }else {
                    mRequest.getStudentIdentificationRequest().setfAdhaarFrontFirebase(firebaseFront);
                    mRequest.getStudentIdentificationRequest().setfAdhaarBackFirebase(firebaseBack);
                    Glide.with(this)
                            .load(mRequest.getStudentIdentificationRequest().getfAdhaarFrontFirebase().getUrl())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into(aadhaarFrontFather);


                    Glide.with(this)
                            .load(mRequest.getStudentIdentificationRequest().getfAdhaarBackFirebase().getUrl())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into(aadhaarBackFather);
                }
            }

        }
    }

}
