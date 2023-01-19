package com.app.mschooling.enrollment.signup.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.activity.ClassExternalListActivity;
import com.app.mschooling.class_section_subject.adapter.OptionalExternalSubjectAdapter;
import com.app.mschooling.class_section_subject.adapter.OptionalSubjectAdapter;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventFileUrl;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.student.AddStudentRequest;
import com.mschooling.transaction.response.qrcode.GetQRCodeResponse;
import com.mschooling.transaction.response.student.AddStudentResponse;
import com.mschooling.transaction.response.subject.GetExternalSubjectResponse;
import com.mschooling.transaction.response.subject.OtherSubjectResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class StudentSignUpFragment extends BaseFragment {

    AddStudentRequest mRequest;
    @BindView(R.id.schoolName)
    TextView schoolName;
    @BindView(R.id.firstName)
    EditText firstName;
    @BindView(R.id.lastName)
    EditText lastName;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.rollNumber)
    EditText rollNumber;
    @BindView(R.id.section)
    TextView section;
    @BindView(R.id.gender)
    Spinner gender;
    @BindView(R.id.dob)
    TextView dob;
    @BindView(R.id.dateLayout)
    RelativeLayout dateLayout;

    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.clazz)
    TextView clazz;
    @BindView(R.id.classLayout)
    LinearLayout classLayout;
    @BindView(R.id.sectionLayout)
    LinearLayout sectionLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.optionalHeading)
    TextView optionalHeading;

    String classId, sectionId;
    String mob;

    GetQRCodeResponse response;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_sign_up, container, false);
        ButterKnife.bind(this, view);
        //  toolbarClick(view);

        response = AppUser.getInstance().getQrCodeDetailResponse();
        schoolName.setText(response.getQrCodeResponse().getSchoolName());

        sectionLayout.setVisibility(View.GONE);
        optionalHeading.setVisibility(View.GONE);

        mRequest = new AddStudentRequest();


        classLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ClassExternalListActivity.class);
                intent.putExtra("schoolId", response.getQrCodeResponse().getSchoolId());
                startActivityForResult(intent, 100);
            }
        });


        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Integer.valueOf(Helper.getCurrentYear()) - 2, Integer.valueOf(Helper.getCurrentMonth()) - 1, Integer.valueOf(Helper.getCurrentDay()));

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf1 = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.getDefault());
                        dob.setText(sdf1.format(myCalendar.getTime()));
                        dob.setError(null);

                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (firstName.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_enter_name));
                    return;
                }
                if (mobile.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_mobile_number));
                    return;
                }
                if (mobile.getText().toString().length() != 10) {
                    dialogError(getString(R.string.please_valid_mobile_number));
                    return;
                }
                if (dob.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_select_dob));
                    return;
                }
                if (clazz.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.select_class));
                    return;
                }


                mRequest.setfName(firstName.getText().toString());
                mRequest.setlName(lastName.getText().toString());
                mRequest.setMobile(mobile.getText().toString());
                mRequest.setEmail(email.getText().toString());
                mRequest.setGender(Common.Gender.valueOf(gender.getSelectedItem().toString()));
                mRequest.setDob(dob.getText().toString());
                mRequest.setRollNumber(rollNumber.getText().toString());
                mRequest.setClassId(classId);
                mRequest.setSectionId(sectionId);

                List<String> list = new ArrayList<>();
                Map<Integer, String> map = OptionalSubjectAdapter.mapId;
                if (map != null)
                    for (Integer key : map.keySet())
                        list.add(map.get(key));

                mRequest.setSubjects(list);
                mob = mobile.getText().toString();
                mRequest.setEncodedSchoolId(response.getQrCodeResponse().getSchoolId());
                apiCallBack(getApiCommonController().signUpStudent(mRequest));
            }
        });

        return view;
    }


    @Subscribe
    public void addStudent(AddStudentResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void getUrl(EventFileUrl uri) {
        Firebase firebase = new Firebase();
        firebase.setUrl(uri.getUrl());
        mRequest.setFirebase(firebase);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 100) {
                    classId = data.getStringExtra("classId");
                    sectionId = data.getStringExtra("sectionId");
                    clazz.setText(data.getStringExtra("className"));
                    section.setText(data.getStringExtra("sectionName"));
                    sectionLayout.setVisibility(View.VISIBLE);
                    apiCallBack(getApiCommonController().getExternalSubjectList(
                            classId, response.getQrCodeResponse().getSchoolId()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Subscribe
    public void getSubject(GetExternalSubjectResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            setListAndAdapter(response);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    void setListAndAdapter(GetExternalSubjectResponse response) {
        recyclerView.setVisibility(View.VISIBLE);
        Map<String, List<OtherSubjectResponse>> optionalSubjectMap = Helper.getOptionalPaperExternal(response);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(new OptionalExternalSubjectAdapter(getActivity(), true, optionalSubjectMap));

        try {
            if (optionalSubjectMap.size() > 0) {
                optionalHeading.setVisibility(View.VISIBLE);
            } else {
                optionalHeading.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
