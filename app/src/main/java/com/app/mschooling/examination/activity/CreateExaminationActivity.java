package com.app.mschooling.examination.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.examination.AddExaminationRequest;
import com.mschooling.transaction.response.examination.AddExaminationResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateExaminationActivity extends BaseActivity {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.type)
    Spinner type;
    @BindView(R.id.submit)
    Button submit;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exam);
        ButterKnife.bind(this);

        id=getIntent().getStringExtra("id");

        if (id==null){
            toolbarClick(getString(R.string.create_examination));
        }else {
            toolbarClick(getString(R.string.update_examination));
            name.setText(getIntent().getStringExtra("name"));

        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().isEmpty()){
                    dialogError(getResources().getString(R.string.please_enter_name));
                    return;
                }

                AddExaminationRequest request=new AddExaminationRequest();
//                request.setId(id);
                request.setName(name.getText().toString().trim());
                if (type.getSelectedItemPosition()==0) {
                    request.setExaminationType(Common.ExaminationType.ASSESSMENT);
                }else {
                    request.setExaminationType(Common.ExaminationType.GRADING);
                }


                apiCallBack(getApiCommonController().createExam(ParameterConstant.getRole(getApplicationContext()),request));
            }
        });

    }


    @Subscribe
    public void add(AddExaminationResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

}
