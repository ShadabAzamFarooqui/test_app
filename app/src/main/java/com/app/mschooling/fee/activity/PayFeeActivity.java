package com.app.mschooling.fee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mschooling.fee.activity.student.StudentFeeDetailListActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.fee.AddStudentFeeRequest;
import com.mschooling.transaction.response.fee.AddStudentFeeResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PayFeeActivity extends BaseActivity {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.fee)
    TextView fee;
    @BindView(R.id.feeLayout)
    LinearLayout feeLayout;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.lateFee)
    EditText lateFee;
    @BindView(R.id.note)
    EditText note;
    @BindView(R.id.submit)
    Button submit;


    String id;
    String feeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_fee);
        ButterKnife.bind(this);
        feeId=getIntent().getStringExtra("feeId");
        if (feeId==null){
            toolbarClick(getResources().getString(R.string.pay_fee));
        }else {
            toolbarClick(getResources().getString(R.string.update_fee));
        }


        name.setText(getIntent().getStringExtra("name")+" ("+getIntent().getStringExtra("enrollmentId")+")");

        feeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), StudentFeeDetailListActivity.class);
                intent.putExtra("intent","selection");
                intent.putExtra("enrollmentId",getIntent().getStringExtra("enrollmentId"));
                startActivityForResult(intent,1);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fee.getText().toString().isEmpty()){
                    dialogError(getResources().getString(R.string.select_fee));
                    return;
                }if (fee.getText().toString().isEmpty()){
                    dialogError(getResources().getString(R.string.enter_fee_amount_msg));
                    return;
                }
                AddStudentFeeRequest request=new AddStudentFeeRequest();
                request.setEnrollmentId(getIntent().getStringExtra("enrollmentId"));
                request.setId(id);
                request.setFeeType(fee.getText().toString());
                request.setAmount(Double.valueOf(amount.getText().toString()));
                if (!lateFee.getText().toString().trim().isEmpty()) {
                    request.setLate(Double.valueOf(lateFee.getText().toString()));
                }
                request.setNote(note.getText().toString());
                apiCallBack(getApiCommonController().addStudentFee(request));
            }
        });
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    fee.setText(data.getStringExtra("name"));
                    id=data.getStringExtra("id");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Subscribe
    public void addFee(AddStudentFeeResponse response){
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
           dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

}