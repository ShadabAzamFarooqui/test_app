package com.app.mschooling.video.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.mschooling.class_section_subject.activity.ClassListActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.video.AddVideoCategoryRequest;
import com.mschooling.transaction.response.video.AddVideoResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddVideoCategoryActivity extends BaseActivity {

    @BindView(R.id.classLayout)
    LinearLayout classLayout;
    @BindView(R.id.clazz)
    TextView clazz;
    @BindView(R.id.cancel)
    ImageView cancel;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.priority)
    EditText priority;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.main_layout)
    LinearLayout mainLayout;

    String id;
    String classId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_video_category_activity);
        ButterKnife.bind(this);

        id=getIntent().getStringExtra("id");
        clazz.setText(getIntent("className"));
        classId=getIntent().getStringExtra("classId");
        if (id!=null){
            name.setText(getIntent().getStringExtra("name"));
            description.setText(getIntent().getStringExtra("description"));
            name.setText(getIntent().getStringExtra("name"));
            priority.setText(getIntent().getStringExtra("priority"));
            toolbarClick(getString(R.string.update_category));
        }else {
            toolbarClick(getString(R.string.add_category));
        }

        showHideCancelButton();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classId=null;
                clazz.setText("");
                showHideCancelButton();
            }
        });

        classLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), ClassListActivity.class), 1);
            }
        });

//        name.setText("Twinkle Twinkle Little Star + More Nursery Rhymes & Kids Songs");
//        description.setText("https://www.youtube.com/watch?v=RciE68Q7PCA");



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().isEmpty()){
                    dialogError(getString(R.string.enter_category_name));
                    return;
                }/*else if (description.getText().toString().trim().isEmpty()){
                    dialogError(getString(R.string.enter_description_w_star));
                    return;
                }*/

                AddVideoCategoryRequest request=new AddVideoCategoryRequest();
                request.setId(id);
                request.setClassId(classId);
                request.setName(name.getText().toString().trim());
                if (priority.getText().toString().trim().isEmpty()) {
                    request.setPriority(0);
                }else {
                    request.setPriority(Integer.parseInt(priority.getText().toString().trim()));
                }
                request.setName(name.getText().toString().trim());
                apiCallBack(getApiCommonController().addVideoCategory(request));
            }
        });


    }

    void showHideCancelButton(){
       /* if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
            if (classId == null) {
                cancel.setVisibility(View.GONE);
            } else {
                cancel.setVisibility(View.VISIBLE);
            }
        }else {
            cancel.setVisibility(View.GONE);
        }*/

        if (classId == null) {
            cancel.setVisibility(View.GONE);
        } else {
            cancel.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void notice(AddVideoResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
//            name.setText("");
//            description.setText("");
//            priority.setText("");
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    classId = data.getStringExtra("id");
                    clazz.setText(data.getStringExtra("name"));
                    showHideCancelButton();
                }
            }
        } catch (Exception ex) {
        }
    }


}

