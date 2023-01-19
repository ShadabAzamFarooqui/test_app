package com.app.mschooling.unused.ocr.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.progress_dialog.MyProgressDialogUploadMultiple;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AadhaarCardActivity extends BaseActivity {

    CardView adhar_card;
    ImageView imageView1;
    ImageView imageView2;
    TextView name;
    TextView dob;
    TextView gender;
    TextView adhar_no;
    TextView address;
    Button submit;
    List<Bitmap> bitmapList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adhar_card);
        toolbarClick(getString(R.string.adhaar_card));

        submit = findViewById(R.id.submit);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        name = findViewById(R.id.name);
        adhar_card = findViewById(R.id.adhar_card);
        dob = findViewById(R.id.dob);
        gender = findViewById(R.id.gender);
        adhar_no = findViewById(R.id.adhar_no);
        address = findViewById(R.id.address);

        String returnString = getIntent().getStringExtra("OCRResponse");
        String image1 = getIntent().getStringExtra("image1");
        String image2 = getIntent().getStringExtra("image2");



        try {
            imageView1.setImageBitmap(Helper.base64ToBitmap(image1));
            imageView2.setImageBitmap(Helper.base64ToBitmap(image2));
            JSONObject jsonObject = new JSONObject(returnString);
            dob.setText(jsonObject.getString("dob"));
            gender.setText(jsonObject.getString("gender"));
            name.setText(jsonObject.getString("name"));
            adhar_no.setText(jsonObject.getString("adhar_no"));
            address.setText(jsonObject.getString("address"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapList = new ArrayList<>();
                bitmapList.add(Helper.base64ToBitmap(image1));
                bitmapList.add(Helper.base64ToBitmap(image2));
                MyProgressDialogUploadMultiple.getInstance(AadhaarCardActivity.this).show();
                MyProgressDialogUploadMultiple.setCount("1","2");



                /*if (getIntent().getStringExtra("type").equals("student")) {
                    AddStudentRequest request = AppUser.getInstance().getAddStudentRequest();
                    request.setAdhaarFrontImage(image1);
                    request.setAdhaarBackImage(image2);
                    request.setAdhaarNo(adhar_no.getText().toString());
                }else if (getIntent().getStringExtra("type").equals("father")) {
                    UpdateStudentRequest request = AppUser.getInstance().getUpdateStudentRequest();
                    request.getParent().setfAdhaarFrontImage(image1);
                    request.getParent().setfAdhaarBackImage(image2);
                    request.getParent().setfAdhaarNo(adhar_no.getText().toString());
                }else if (getIntent().getStringExtra("type").equals("mother")) {
                    UpdateStudentRequest request = AppUser.getInstance().getUpdateStudentRequest();
                    request.getParent().setmAdhaarFrontImage(image1);
                    request.getParent().setmAdhaarBackImage(image2);
                    request.getParent().setmAdhaarNo(adhar_no.getText().toString());
                }
                finish();*/
            }
        });

    }




}
