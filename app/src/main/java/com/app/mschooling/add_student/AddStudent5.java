package com.app.mschooling.add_student;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventFileUrl;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ImagePicker;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.student.UpdateStudentResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddStudent5 extends BaseActivity {

    Button submit;
    Spinner transport;
    Spinner type;
    Spinner hobby;
    @BindView(R.id.category)
    Spinner category;
    @BindView(R.id.religion)
    Spinner religion;
    @BindView(R.id.bloodGroup)
    Spinner bloodGroup;


    @BindView(R.id.dateLayout)
    RelativeLayout dateLayout;
    @BindView(R.id.doa)
    TextView doa;
    @BindView(R.id.lastSchoolSpinner)
    Spinner lastSchoolSpinner;
    @BindView(R.id.lastSchool)
    EditText lastSchool;
    @BindView(R.id.lastClassSpinner)
    Spinner lastClassSpinner;
    @BindView(R.id.lastClass)
    EditText lastClass;

    @BindView(R.id.uploadLayout)
    LinearLayout uploadLayout;
    @BindView(R.id.image)
    ImageView image;

    String base64 = "";

    UpdateStudentRequest mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_student5);
        toolbarClick(getString(R.string.tool_other_details));
        ButterKnife.bind(this);
        submit = findViewById(R.id.submit);
        transport = findViewById(R.id.transport);
        type = findViewById(R.id.type);
        hobby = findViewById(R.id.hobby);

        lastSchool.setVisibility(View.GONE);
        lastClass.setVisibility(View.GONE);
        uploadLayout.setVisibility(View.GONE);

        mRequest = AppUser.getInstance().getUpdateStudentRequest();
        mRequest = new UpdateStudentRequest();
        mRequest.getStudentBasicRequest().setEnrollmentId(getIntent().getStringExtra("id"));

        doa.setText(Helper.getCurrentDate());

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddStudent5.this, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf1 = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.getDefault());
                        doa.setText(sdf1.format(myCalendar.getTime()));

                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });


        lastSchoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    lastSchool.setVisibility(View.GONE);
                    uploadLayout.setVisibility(View.GONE);
                } else {
                    lastClassSpinner.setSelection(position);
                    lastSchool.setVisibility(View.VISIBLE);
                    if (lastClassSpinner.getSelectedItemPosition() != 0) {
                        uploadLayout.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        lastClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    lastSchoolSpinner.setSelection(position);
                    lastClass.setVisibility(View.GONE);
                    uploadLayout.setVisibility(View.GONE);
                } else {
                    lastClass.setVisibility(View.VISIBLE);
                    if (lastSchoolSpinner.getSelectedItemPosition() != 0) {
                        uploadLayout.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lastSchoolSpinner.getSelectedItemPosition() != 0 && lastSchool.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.enter_your_last_school));
                    return;
                }
                if (lastClassSpinner.getSelectedItemPosition() != 0 && lastClass.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.enter_your_last_class));
                    return;
                }

                mRequest.getStudentOtherRequest().setDoa(doa.getText().toString());
                mRequest.getStudentOtherRequest().setCategory(category.getSelectedItem().toString());
                mRequest.getStudentOtherRequest().setReligion(religion.getSelectedItem().toString());
                mRequest.getStudentOtherRequest().setBloodGroup(bloodGroup.getSelectedItem().toString());
                mRequest.getStudentOtherRequest().setConvenience(transport.getSelectedItem().toString());
                mRequest.getStudentOtherRequest().setType(type.getSelectedItem().toString());
                mRequest.getStudentOtherRequest().setHobby(hobby.getSelectedItem().toString());

//                mRequest.getStudentOtherRequest().setLastSchoolName(lastSchool.getText().toString());
//                mRequest.getStudentOtherRequest().setLastClass(lastClass.getText().toString());

                apiCallBack(getApiCommonController().updateStudent(mRequest));
            }
        });
    }


    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccessAddUser("student", response.getMessage().getMessage());
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                try {
                    fileUri = data.getData();
                    image.setImageURI(fileUri);
                    Bitmap photo = ImagePicker.getImageFromResult(getApplicationContext(), resultCode, data);
                    base64 = Helper.bitmapToBase64(photo);
//                        image.setImageBitmap(photo);
//                        uploadFile(ParameterConstant.STUDENT_MARKSHEET, imageName, this.fileUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == REQUEST_CAMERA) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                image.setImageURI(fileUri);
                base64 = Helper.bitmapToBase64(BitmapFactory.decodeFile(fileUri.getPath(), options));
//                    image.setImageBitmap(BitmapFactory.decodeFile(fileUri.getPath(), options));
//                    uploadFile(ParameterConstant.PROFILE,imageName,this.fileUri);
            }
        }

}

    @Subscribe
    public void getUrl(EventFileUrl uri) {
//        mRequest.setMarksheetImage(uri.toString());
    }


}
