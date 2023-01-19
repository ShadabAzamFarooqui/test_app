package com.app.mschooling.unused.ocr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mschooling.other.activity.ShowImageActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.progress_dialog.MyProgressDialogUploadMultiple;

import org.json.JSONObject;

public class PanCardActivity extends BaseActivity {

    Button submit;
    ImageView imageView;
    TextView pan_name;
    TextView pan_type;
    TextView pan_sub_type;
    TextView pan_dob;
    TextView pan_dod_txt;
    TextView pan_no;
    TextView pan_father;
    LinearLayout pan_father_layout;
    String image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pan_card);
        toolbarClick(getString(R.string.pan_card));

        submit = findViewById(R.id.submit);
        imageView = findViewById(R.id.imageView);
        pan_name = findViewById(R.id.pan_name);
        pan_type = findViewById(R.id.pan_type);
        pan_sub_type = findViewById(R.id.pan_sub_type);
        pan_dob = findViewById(R.id.pan_dob);
        pan_dod_txt = findViewById(R.id.pan_dod_txt);
        pan_no = findViewById(R.id.pan_no);
        pan_father = findViewById(R.id.pan_father);
        pan_father_layout = findViewById(R.id.pan_father_layout);
        try {
            String returnString = getIntent().getStringExtra("OCRResponse");
            image = getIntent().getStringExtra("image");
            imageView.setImageBitmap(Helper.base64ToBitmap(image));
            JSONObject jsonObject = new JSONObject(returnString);
            pan_name.setText(jsonObject.getString("name"));
            pan_type.setText(jsonObject.getString("type"));
            pan_sub_type.setText(jsonObject.getString("subType"));
            if (pan_sub_type.getText().toString().equalsIgnoreCase("Individual_pan")){
                pan_dod_txt.setText("DOB");
                pan_father_layout.setVisibility(View.VISIBLE);
                pan_father.setText(jsonObject.getString("father"));
                pan_dob.setText(jsonObject.getString("dob"));
//                        father, dob
            }else {
                pan_dod_txt.setText(getString(R.string.incorporation_date));
                pan_father_layout.setVisibility(View.GONE);
                pan_dob.setText(jsonObject.getString("incorporationDate"));
//                        incorporation1
            }

            pan_no.setText(jsonObject.getString("panNo"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ShowImageActivity.class);
                intent.putExtra("name","Pan Card");
                intent.putExtra("image",getIntent().getStringExtra("image"));
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (getIntent().getStringExtra("type").equals("father")){
                    UpdateStudentRequest request= AppUser.getInstance().getUpdateStudentRequest();
                    request.getParent().setfPanImage(getIntent().getStringExtra("image"));
                    request.getParent().setfPanNo(pan_no.getText().toString());
                    finish();
                }else if (getIntent().getStringExtra("type").equals("mother")){
                    UpdateStudentRequest request= AppUser.getInstance().getUpdateStudentRequest();
                    request.getParent().setmPanImage(getIntent().getStringExtra("image"));
                    request.getParent().setmPanNo(pan_no.getText().toString());
                    finish();
                }*/
                MyProgressDialogUploadMultiple.getInstance(PanCardActivity.this).show();
                MyProgressDialogUploadMultiple.setCount("1","1");


            }
        });

    }





































}
