package com.app.mschooling.students.profile.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.mschooling.home.student.activity.StudentMainActivity;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.student.request.StudentOtherRequest;
import com.mschooling.transaction.common.student.response.StudentOtherResponse;
import com.mschooling.transaction.request.profile.UpdateStudentProfileRequest;
import com.mschooling.transaction.response.profile.GetStudentProfileResponse;
import com.mschooling.transaction.response.profile.UpdateStudentProfileResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentProfileOtherFragment extends BaseFragment {

    GetStudentProfileResponse response;
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
    boolean update;

    public StudentProfileOtherFragment() {

    }
    public void init(GetStudentProfileResponse response, boolean update) {
        if (response.getStudentOtherResponse() == null) {
            response.setStudentOtherResponse(new StudentOtherResponse());
        }
        this.update = update;
        this.response = response;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);
        lastSchool.setVisibility(View.GONE);
        lastClass.setVisibility(View.GONE);
        uploadLayout.setVisibility(View.GONE);

        StudentOtherResponse studentOtherResponse = response.getStudentOtherResponse();

        doa.setText(studentOtherResponse.getDoa());
        category.setSelection(getPosition(R.array.category, studentOtherResponse.getCategory()));
        religion.setSelection(getPosition(R.array.religion, studentOtherResponse.getReligion()));
        bloodGroup.setSelection(getPosition(R.array.bloodGroup, studentOtherResponse.getBloodGroup()));
        transport.setSelection(getPosition(R.array.transport, studentOtherResponse.getConvenience()));
        type.setSelection(getPosition(R.array.student_type, studentOtherResponse.getType()));
        hobby.setSelection(getPosition(R.array.sport, studentOtherResponse.getHobby()));


        /*dateLayout.setOnClickListener(new View.OnClickListener() {
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
                        SimpleDateFormat sdf1 = new SimpleDateFormat(ParameterConstant.DATE_FORMAT, Locale.getDefault());
                        doa.setText(sdf1.format(myCalendar.getTime()));

                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });*/


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
                    dialogError(getString(R.string.enter_your_last_class));
                    return;
                }

                StudentOtherRequest studentOtherRequest = new StudentOtherRequest();


                studentOtherRequest.setDoa(doa.getText().toString());
                studentOtherRequest.setCategory(category.getSelectedItem().toString());
                studentOtherRequest.setReligion(religion.getSelectedItem().toString());
                studentOtherRequest.setBloodGroup(bloodGroup.getSelectedItem().toString());
                studentOtherRequest.setConvenience(transport.getSelectedItem().toString());
                studentOtherRequest.setType(type.getSelectedItem().toString());
                studentOtherRequest.setHobby(hobby.getSelectedItem().toString());

                UpdateStudentProfileRequest request = new UpdateStudentProfileRequest();
                request.setStudentOtherRequest(studentOtherRequest);
                apiCallBack(getApiCommonController().updateStudentProfile(request));
            }
        });
        return view;
    }



    @Subscribe
    public void uploadResponse(UpdateStudentProfileResponse updateStudentProfileResponse) {
        if (Status.SUCCESS.value() == updateStudentProfileResponse.getStatus().value()) {
            Preferences.getInstance(getContext()).setProfileComplete(true);
            Intent intent = new Intent(getContext(), StudentMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            dialogError(updateStudentProfileResponse.getMessage().getMessage());
        }
    }
}

