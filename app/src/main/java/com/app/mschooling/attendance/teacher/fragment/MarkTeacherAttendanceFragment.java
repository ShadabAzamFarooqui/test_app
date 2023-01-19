package com.app.mschooling.attendance.teacher.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.mschooling.attendance.teacher.activity.TeacherAttendanceDetailActivity;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.attendance.AddStandaloneTeacherAttendanceRequest;
import com.mschooling.transaction.request.attendance.TeacherAttendanceRequest;
import com.mschooling.transaction.response.attendance.AddStandaloneTeacherAttendanceResponse;
import com.mschooling.transaction.response.attendance.AttendanceResponse;
import com.mschooling.transaction.response.attendance.GetStandaloneTeacherAttendanceResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cdflynn.android.library.checkview.CheckView;

public class MarkTeacherAttendanceFragment extends BaseFragment {


     @BindView(R.id.check)
    CheckView check;
    @BindView(R.id.change)
    Button change;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.attendance)
    LinearLayout attendance;
    @BindView(R.id.submitted)
    LinearLayout submitted;
    @BindView(R.id.preview)
    LinearLayout preview;
    @BindView(R.id.attendanceLayout)
    LinearLayout attendanceLayout;
    @BindView(R.id.attendanceText)
    TextView attendanceText;
    @BindView(R.id.date)
    TextView date;

    String[] attendanceArray;
    int position = 0;

    GetStandaloneTeacherAttendanceResponse response;
    AttendanceResponse teacherAttendance;

    boolean isEnabled=true;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark_teacher_attendance, container, false);
        ButterKnife.bind(this, view);
        toolbarClick(view, getString(R.string.attendance));
//        isEnabled = Preferences.getInstance(getContext()).getConfiguration().
//                getCommonSetup().getSettingSetup().getTeacherAttendancePrivacy().equals("ENABLED");
        attendanceArray = getActivity().getResources().getStringArray(R.array.attendance);
        if (!isEnabled) {
            attendanceArray[0] = "Not marked";
            submit.setVisibility(View.GONE);
        }

        date.setText(Helper.getCurrentDate());

        apiCallBack(getApiCommonController().getSingleTeacherAttendance(Helper.getCurrentDate()));

        teacherAttendance = new AttendanceResponse();


        attendanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnabled) {
                    if (position == 3) {
                        position = 1;
                    } else {
                        position++;
                    }
                    setView(position);
                    if (position == 1) {
                        teacherAttendance.setAttendance(Common.Attendance.P);
                    } else if (position == 2) {
                        teacherAttendance.setAttendance(Common.Attendance.A);
                    } else if (position == 3) {
                        teacherAttendance.setAttendance(Common.Attendance.L);
                    }
                }
            }
        });


        attendance.setVisibility(View.GONE);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendance.setVisibility(View.VISIBLE);
                submitted.setVisibility(View.GONE);
                setView(position);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    dialogError(getString(R.string.mark_attendance));
                    return;
                }
                response.getTeacherAttendanceWrapperResponse().getTeacherAttendanceResponse().setAttendanceResponse( teacherAttendance);
                AddStandaloneTeacherAttendanceRequest request = new AddStandaloneTeacherAttendanceRequest();
                request.setDate(date.getText().toString());
                TeacherAttendanceRequest teacherAttendanceRequest=new TeacherAttendanceRequest();
                teacherAttendanceRequest.setAttendance(teacherAttendance.getAttendance());
                request.setTeacherAttendanceRequest(teacherAttendanceRequest);
                apiCallBack(getApiCommonController().submitSingleTeacherAttendance(request));
            }
        });

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TeacherAttendanceDetailActivity.class));
            }
        });

//        check.check();

        return view;
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    void setView(int position) {
        if (position == 0) {
            attendanceLayout.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.circle_gallery));
        } else {
            attendanceLayout.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.circle_military));
        }
        attendanceText.setText(attendanceArray[position]);

    }

    @Subscribe
    public void getResponse(GetStandaloneTeacherAttendanceResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            teacherAttendance = response.getTeacherAttendanceWrapperResponse().getTeacherAttendanceResponse().getAttendanceResponse();
            attendance.setVisibility(View.VISIBLE);
            if (teacherAttendance.getAttendance() == null) {
                position = 0;
                setView(0);
            } else {
                if (teacherAttendance.getAttendance() == Common.Attendance.P) {
                    position = 1;
                    setView(1);
                } else if (teacherAttendance.getAttendance() == Common.Attendance.A) {
                    position = 2;
                    setView(2);
                } else if (teacherAttendance.getAttendance() == Common.Attendance.L) {
                    position = 3;
                    setView(3);
                }
                check.check();
                if (isEnabled) {
                    attendance.setVisibility(View.GONE);
                    submitted.setVisibility(View.VISIBLE);
                }

            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void submitAttendance(AddStandaloneTeacherAttendanceResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            check.check();
            attendance.setVisibility(View.GONE);
            submitted.setVisibility(View.VISIBLE);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

}

