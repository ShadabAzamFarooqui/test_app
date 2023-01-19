package com.app.mschooling.attendance.teacher.activity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.mschooling.attendance.teacher.adapter.MarkTeacherAttendanceAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityMarkTeachersAttendanceBinding;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.attendance.AddTeacherAttendanceRequest;
import com.mschooling.transaction.request.attendance.TeacherAttendanceRequest;
import com.mschooling.transaction.response.attendance.AddTeacherAttendanceResponse;
import com.mschooling.transaction.response.attendance.GetTeacherAttendanceResponse;
import com.mschooling.transaction.response.attendance.TeacherAttendanceResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MarkTeachersAttendanceActivity extends BaseActivity {
    GetTeacherAttendanceResponse response;
    MarkTeacherAttendanceAdapter adapter;

    ActivityMarkTeachersAttendanceBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mark_teachers_attendance);

        toolbarClick(getString(R.string.attendance));
        binding.date.setText(Helper.getCurrentDate());


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
                    adapter.filteredList = response.getTeacherAttendanceWrapperResponse().getTeacherAttendanceResponses();
                    adapter.responseList = response.getTeacherAttendanceWrapperResponse().getTeacherAttendanceResponses();
                    adapter.getFilter().filter(s.toString());
                }


            }
        });


        binding.date.setOnClickListener(view -> dateDialog());
        binding.dateLayout.setOnClickListener(view -> dateDialog());

        binding.submit.setOnClickListener(v -> {
            AddTeacherAttendanceRequest request = new AddTeacherAttendanceRequest();
            List<TeacherAttendanceRequest> teacherAttendanceRequests = new ArrayList<>();
            List<TeacherAttendanceResponse> list = response.getTeacherAttendanceWrapperResponse().getTeacherAttendanceResponses();
            for (int i = 0; i < list.size(); i++) {
                teacherAttendanceRequests.add(new TeacherAttendanceRequest(list.get(i).getEnrollmentId(),
                        list.get(i).getAttendanceResponse().getAttendance()));
            }
            request.setTeacherAttendanceRequests(teacherAttendanceRequests);
            request.setDate(binding.date.getText().toString());
            apiCallBack(getApiCommonController().submitAllTeacherAttendance(request));
        });


    }

    void apiCall(String date) {
        apiCallBack(getApiCommonController().getAbsence(date, null));
    }

    @Subscribe
    public void getResponse(GetTeacherAttendanceResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            binding.recyclerView.setHasFixedSize(true);
            binding.recyclerView.setFocusable(false);
            binding.recyclerView.setNestedScrollingEnabled(false);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new MarkTeacherAttendanceAdapter(this, this.response.getTeacherAttendanceWrapperResponse().getTeacherAttendanceResponses(), binding.date.getText().toString());
            binding.recyclerView.setAdapter(adapter);
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void submitAttendance(AddTeacherAttendanceResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    void dateDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        DatePickerDialog datePickerDialog = new DatePickerDialog(MarkTeachersAttendanceActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(year, monthOfYear, dayOfMonth);
            binding.date.setText(new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.US).format(calendar1.getTime()));
            apiCall(binding.date.getText().toString());
        }, currentYear, currentMonth, currentDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

}
