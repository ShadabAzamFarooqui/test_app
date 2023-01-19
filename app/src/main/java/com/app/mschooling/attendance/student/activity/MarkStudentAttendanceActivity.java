package com.app.mschooling.attendance.student.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.mschooling.attendance.student.adapter.MarkStudentAttendanceAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityMarkStudentAttendanceBinding;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.attendance.AddStudentAttendanceRequest;
import com.mschooling.transaction.request.attendance.StudentAttendanceRequest;
import com.mschooling.transaction.response.attendance.AddStudentAttendanceResponse;
import com.mschooling.transaction.response.attendance.AttendanceResponse;
import com.mschooling.transaction.response.attendance.GetStudentAttendanceResponse;
import com.mschooling.transaction.response.attendance.StudentAttendanceResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MarkStudentAttendanceActivity extends BaseActivity {
    GetStudentAttendanceResponse response;
    MarkStudentAttendanceAdapter adapter;
    AttendanceModel attendanceModel;


    ActivityMarkStudentAttendanceBinding binding;

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mark_student_attendance);
//        binding = ActivityAttendanceStudentListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbarClick(getString(R.string.attendance));
        binding.date.setText(Helper.getCurrentDate());


        attendanceModel = new AttendanceModel();
        attendanceModel.setClassId(getIntent().getStringExtra("classId"));
        attendanceModel.setClassName(getIntent().getStringExtra("className"));
        attendanceModel.setSectionId(getIntent().getStringExtra("sectionId"));
        attendanceModel.setSectionName(getIntent().getStringExtra("sectionName"));
        attendanceModel.setSubjectId(getIntent().getStringExtra("subjectId"));
        attendanceModel.setSubjectName(getIntent().getStringExtra("subjectName"));

        apiCall(Helper.getCurrentDate());
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter != null) {
                    adapter.filteredList = response.getStudentAttendanceWrapperResponse().getStudentAttendanceResponses();
                    adapter.responseList = response.getStudentAttendanceWrapperResponse().getStudentAttendanceResponses();
                    adapter.getFilter().filter(s.toString());
                }
            }
        });

        binding.clearLayout.setOnClickListener(v -> {
            if (response != null) {
                for (int i = 0; i < response.getStudentAttendanceWrapperResponse().getStudentAttendanceResponses().size(); i++) {
                    response.getStudentAttendanceWrapperResponse().getStudentAttendanceResponses().get(i).setAttendanceResponse(new AttendanceResponse());
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

        });


        binding.date.setOnClickListener(view -> dateDialog());
        binding.dateLayout.setOnClickListener(view -> dateDialog());

        binding.submit.setOnClickListener(v -> saveAttendance());


    }

    void apiCall(String date) {
        apiCallBack(getApiCommonController().getAttendanceStudent(
                ParameterConstant.getRole(getApplicationContext()),
                date,
                null,
                attendanceModel.getClassId(),
                attendanceModel.getSectionId(),
                attendanceModel.getSubjectId()));
    }

    void saveAttendance() {
        if (response == null) {
            return;
        }
        AddStudentAttendanceRequest request = new AddStudentAttendanceRequest();
        request.setDate(binding.date.getText().toString());
        request.setClassId(attendanceModel.getClassId());
        request.setSectionId(attendanceModel.getSectionId());
        request.setSubjectId(attendanceModel.getSubjectId());
        List<StudentAttendanceRequest> list = new ArrayList<>();
        List<StudentAttendanceResponse> attendanceResponseList = response.getStudentAttendanceWrapperResponse().getStudentAttendanceResponses();
        for (int i = 0; i < attendanceResponseList.size(); i++) {
            list.add(new StudentAttendanceRequest(attendanceResponseList.get(i).getEnrollmentId(), attendanceResponseList.get(i).getAttendanceResponse().getAttendance()));
        }
        request.setStudentAttendanceRequests(list);
        apiCallBack(getApiCommonController().submitAttendance(request));
    }

    @Subscribe
    public void getAttendance(GetStudentAttendanceResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            binding.recyclerView.setHasFixedSize(true);
            binding.recyclerView.setFocusable(false);
            binding.recyclerView.setNestedScrollingEnabled(false);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new MarkStudentAttendanceAdapter(this, this.response.getStudentAttendanceWrapperResponse().getStudentAttendanceResponses());
            binding.recyclerView.setAdapter(adapter);
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void submitAttendance(AddStudentAttendanceResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    void dateDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(MarkStudentAttendanceActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(year, monthOfYear, dayOfMonth);
            binding.date.setText(new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.US).format(calendar1.getTime()));
            apiCall(binding.date.getText().toString());

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.TEACHER.value())) {
            long millis = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000;
            datePickerDialog.getDatePicker().setMinDate(millis);
        }

        datePickerDialog.show();
    }


    static class AttendanceModel {
        private String classId;
        private String className;
        private String sectionId;
        private String sectionName;
        private String subjectId;
        private String subjectName;


        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getSectionId() {
            return sectionId;
        }

        public void setSectionId(String sectionId) {
            this.sectionId = sectionId;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }
    }

}
