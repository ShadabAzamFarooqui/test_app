package com.app.mschooling.students.detail.fragment;


import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.activity.ClassSectionSelectionActivity;
import com.app.mschooling.class_section_subject.adapter.OptionalSubjectAdapter;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.student.request.StudentBasicRequest;
import com.mschooling.transaction.common.student.response.StudentBasicResponse;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.student.GetStudentDetailResponse;
import com.mschooling.transaction.response.student.UpdateStudentResponse;
import com.mschooling.transaction.response.subject.GetStandaloneSubjectResponse;
import com.mschooling.transaction.response.subject.StandaloneSubject;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class BasicDetailFragment extends BaseFragment {

    StudentBasicResponse response;
    String enrollmentId;

    @BindView(R.id.image)
    CircleImageView image;
    @BindView(R.id.captureImage)
    RelativeLayout captureImage;
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
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    UpdateStudentRequest mRequest = new UpdateStudentRequest();
    String classId, sectionId;
    boolean update;

    public BasicDetailFragment(GetStudentDetailResponse response,boolean update) {
        this.response = response.getStudentDetailResponse().getStudentBasicResponse();
        enrollmentId = response.getStudentDetailResponse().getStudentBasicResponse().getEnrollmentId();
        this.update=update;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basic_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);
        submit.setVisibility(View.GONE);
        imageName = Helper.getRandom();
        firstName.setText(response.getfName());
        lastName.setText(response.getlName());
        mobile.setText(response.getMobile());
        rollNumber.setText(response.getRollNumber());
        email.setText(response.getEmail());
        if (response.getGender().value().equals(Common.Gender.Male.value())) {
            gender.setSelection(0);
        } else if (response.getGender().value().equals(Common.Gender.Female.value())) {
            gender.setSelection(1);
        } else {
            gender.setSelection(2);
        }
        dob.setText(response.getDob());
        clazz.setText(response.getClassName());
        section.setText(response.getSectionName());
        classId = response.getClassId();
        sectionId = response.getSectionId();


        optionalHeading.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);


        classLayout.setOnClickListener(view1 -> startActivityForResult(new Intent(getActivity(), ClassSectionSelectionActivity.class), 100));

        dateLayout.setOnClickListener(view12 -> {
            final Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(Integer.parseInt(Helper.getCurrentYear()) - 2, Helper.getCurrentMonth() - 1, Helper.getCurrentDay());

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view121, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf1 = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.getDefault());
                dob.setText(sdf1.format(myCalendar.getTime()));
                dob.setError(null);

            }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
            datePickerDialog.show();
        });


        captureImage.setOnClickListener(v -> dialogPlus());


        submit.setOnClickListener(v -> {


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

            StudentBasicRequest request = new StudentBasicRequest();
            request.setEnrollmentId(response.getEnrollmentId());
            request.setfName(firstName.getText().toString());
            request.setlName(lastName.getText().toString());
            request.setMobile(mobile.getText().toString());
            request.setEmail(email.getText().toString());
            request.setRollNumber(rollNumber.getText().toString());
            request.setGender(Common.Gender.valueOf(gender.getSelectedItem().toString()));
            request.setDob(dob.getText().toString());
            request.setClassId(classId);
            request.setSectionId(sectionId);
            List<String> list = new ArrayList<>();
            if (OptionalSubjectAdapter.mapId != null) {
                Map<Integer, String> map = OptionalSubjectAdapter.mapId;
                for (Integer key : map.keySet()) {
                    list.add(map.get(key));
                }
                request.setSubjects(list);
            } else {
                request.setSubjects(new ArrayList<>());
            }
            mRequest.setStudentBasicRequest(request);

            apiCallBack(getApiCommonController().updateStudent(mRequest));
        });

        subjectApi();

        return view;
    }

    void subjectApi() {
        apiCallBackWithout(getApiCommonController().getSubjectList(ParameterConstant.getRole(getActivity()), classId, sectionId));
    }


    @Subscribe
    public void getSubject(GetStandaloneSubjectResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            if (update) {
                submit.setVisibility(View.VISIBLE);
            }
            setListAndAdapter(response);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    void setListAndAdapter(GetStandaloneSubjectResponse response) {
        recyclerView.setVisibility(View.VISIBLE);
        OptionalSubjectAdapter.mapPosition = new Hashtable<>();
        Map<String, List<StandaloneSubject>> optionalSubjectMap = Helper.getOptionalPaper(response);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        for (int i = 0; i < this.response.getSubjects().size(); i++) {
            for (int j = 0; j < response.getStandaloneSubjectResponses().size(); j++) {
                for (int k = 0; k < response.getStandaloneSubjectResponses().get(j).getStandaloneSubjects().size(); k++) {
                    if (this.response.getSubjects().get(i).equals(response.getStandaloneSubjectResponses().get(j).getStandaloneSubjects().get(k).getId())) {
                        OptionalSubjectAdapter.mapPosition.put(i, k);
                    }
                }
            }
        }
        recyclerView.setAdapter(new OptionalSubjectAdapter(getActivity(), update, optionalSubjectMap));
        if (optionalSubjectMap.size() > 0) {
            optionalHeading.setVisibility(View.VISIBLE);
        } else {
            optionalHeading.setVisibility(View.GONE);
        }
    }


    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                 if (requestCode == 100) {
                    classId = data.getStringExtra("classId");
                    clazz.setText(data.getStringExtra("className"));
                    sectionId=data.getStringExtra("sectionId");
                    section.setText(data.getStringExtra("sectionName"));
                    sectionLayout.setVisibility(View.VISIBLE);
                    subjectApi();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


}

