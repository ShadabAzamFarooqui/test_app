package com.app.mschooling.attendance.student.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.mschooling.attendance.student.adapter.CustomSpinnerAdapter;
import com.app.mschooling.attendance.student.adapter.StudentAttendanceDetailAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.class_section_subject.adapter.OptionalExternalSubjectAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityStudentAttendanceDetailBinding;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.progress_dialog.MyProgressDialog;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.attendance.GetAttendanceMonthResponse;
import com.mschooling.transaction.response.attendance.GetStudentAttendanceDetailResponse;

import org.greenrobot.eventbus.Subscribe;

public class StudentAttendanceDetailActivity extends BaseActivity {

    GetStudentAttendanceDetailResponse response;
    String enrollmentId;
    ActivityStudentAttendanceDetailBinding binding;
    GetAttendanceMonthResponse monthResponse;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_student_attendance_detail);
        enrollmentId = getIntent().getStringExtra("enrollmentId");
        apiCallBack(getApiCommonController().getMonth());
        if (enrollmentId == null) {
            toolbarClick(getString(R.string.attendance));
            enrollmentId = Preferences.getInstance(getApplicationContext()).getEnrollmentId();
        } else {
            toolbarClick(getIntent().getStringExtra("name"));
        }

        binding.monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                apiCallBack(getApiCommonController().getAttendanceStudent(ParameterConstant.getRole(getApplicationContext()), monthResponse.getAttendanceMonthResponses().get(binding.monthSpinner.getSelectedItemPosition()).getMonth() ,monthResponse.getAttendanceMonthResponses().get(binding.monthSpinner.getSelectedItemPosition()).getYear(), enrollmentId));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Subscribe
    public void getResponse(GetStudentAttendanceDetailResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            this.response = response;

            MyProgressDialog.setDismiss();
            if (response.getStatus().value().equals(Status.SUCCESS.value())) {

                binding.recyclerView.setHasFixedSize(true);
                binding.recyclerView.setFocusable(false);
                binding.recyclerView.setNestedScrollingEnabled(false);
                binding.recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
                binding.recyclerView.setAdapter(new StudentAttendanceDetailAdapter(this, response.getStudentAttendanceDetailWrapperResponse().getStudentAttendanceDetailResponse()));


            }
        } else {
            dialogError(response.getMessage().getMessage());
        }

    }

    @Subscribe
    public void getMonth(GetAttendanceMonthResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            this.monthResponse=response;
            CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(this, response.getAttendanceMonthResponses());
            binding.monthSpinner.setAdapter(spinnerAdapter);
            binding.monthSpinner.setSelection(response.getCurrentMonthIndex());
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


}
