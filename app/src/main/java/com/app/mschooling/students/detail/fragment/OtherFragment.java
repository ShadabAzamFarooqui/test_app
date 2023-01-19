package com.app.mschooling.students.detail.fragment;


import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.student.response.StudentOtherResponse;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.student.GetStudentDetailResponse;
import com.mschooling.transaction.response.student.UpdateStudentResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtherFragment extends BaseFragment {

    UpdateStudentRequest mRequest = new UpdateStudentRequest();
    StudentOtherResponse response;
    String base64 = "";

    @BindView(R.id.dob_tv)
    TextView dobTv;
    @BindView(R.id.doa)
    TextView doa;
    @BindView(R.id.datePicker)
    ImageView datePicker;
    @BindView(R.id.dateLayout)
    RelativeLayout dateLayout;
    @BindView(R.id.category)
    Spinner category;
    @BindView(R.id.religion)
    Spinner religion;
    @BindView(R.id.bloodGroup)
    Spinner bloodGroup;
    @BindView(R.id.transport)
    Spinner transport;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.type)
    Spinner type;
    @BindView(R.id.hobby)
    Spinner hobby;
    @BindView(R.id.lastSchoolSpinner)
    Spinner lastSchoolSpinner;
    @BindView(R.id.lastSchool)
    EditText lastSchool;
    @BindView(R.id.lastClassSpinner)
    Spinner lastClassSpinner;
    @BindView(R.id.lastClass)
    EditText lastClass;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.upload)
    LinearLayout upload;
    @BindView(R.id.uploadLayout)
    LinearLayout uploadLayout;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    String enrollmentId;
    boolean update ;
    public OtherFragment(GetStudentDetailResponse response,boolean update) {
        this.response = response.getStudentDetailResponse().getStudentOtherResponse();
        enrollmentId=response.getStudentDetailResponse().getStudentBasicResponse().getEnrollmentId();
        this.update=update;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);
        lastSchool.setVisibility(View.GONE);
        lastClass.setVisibility(View.GONE);
        uploadLayout.setVisibility(View.GONE);

        AppUser.getInstance().setUpdateStudentRequest(mRequest);
        mRequest = new UpdateStudentRequest();
        mRequest.getStudentBasicRequest().setEnrollmentId(enrollmentId);

        doa.setText(response.getDoa());
        category.setSelection(getPosition(R.array.category, response.getCategory()));
        religion.setSelection(getPosition(R.array.religion, response.getReligion()));
        bloodGroup.setSelection(getPosition(R.array.bloodGroup, response.getBloodGroup()));
        transport.setSelection(getPosition(R.array.transport, response.getConvenience()));
        type.setSelection(getPosition(R.array.student_type, response.getType()));
        hobby.setSelection(getPosition(R.array.sport, response.getHobby()));

       /* if (response.getLastSchoolName() != null) {
            if (response.getLastSchoolName().equals(getString(R.string.first_school))) {
                lastSchool.setVisibility(View.GONE);
                uploadLayout.setVisibility(View.GONE);
            }
        }

        if (response.getLastClass() != null) {
            if (response.getLastClass().equals(getString(R.string.first_admission))) {
                lastClass.setVisibility(View.GONE);
                uploadLayout.setVisibility(View.GONE);
            }
        }*/


        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPlus();
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
                    dialogError(getString(R.string.enter_your_last_school));
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
        return view;
    }


    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}

