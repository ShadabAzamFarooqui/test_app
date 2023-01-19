package com.app.mschooling.prayer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.prayer.AddPrayerRequest;
import com.mschooling.transaction.response.prayer.AddPrayerResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddPrayerActivity extends BaseActivity {

    @BindView(R.id.tittle)
    EditText title;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.submit)
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prayer);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("id")==null){
            toolbarClick(getResources().getString(R.string.add_prayer));
        }else {
            toolbarClick(getResources().getString(R.string.update_prayer));
            title.setText(getIntent().getStringExtra("title"));
            content.setText(getIntent().getStringExtra("content"));
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().trim().isEmpty()){
                    dialogError(getResources().getString(R.string.enter_title));
                    return;
                } if (content.getText().toString().trim().isEmpty()){
                    dialogError(getResources().getString(R.string.enter_prayer));
                    return;
                }
                AddPrayerRequest request=new AddPrayerRequest();
                request.setId(getIntent().getStringExtra("id"));
                request.setTitle(title.getText().toString());
                request.setContent(content.getText().toString());
                apiCallBack(getApiCommonController().addPrayer(request));
            }
        });
    }





    @Subscribe
    public void getStudentList(AddPrayerResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

}