package com.app.mschooling.attendance.student.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.attendance.student.adapter.CustomSpinnerAdapter;
import com.app.mschooling.attendance.student.adapter.StudentAttendanceDetailAdapter;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.FragmentAttendanceStudentBinding;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.attendance.GetAttendanceMonthResponse;
import com.mschooling.transaction.response.attendance.GetStudentAttendanceDetailResponse;
import com.mschooling.transaction.response.subject.GetStandaloneSubjectResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentAttendanceFragment extends BaseFragment {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    GetStudentAttendanceDetailResponse response;
    @BindView(R.id.monthSpinner)
    Spinner monthSpinner;
    @BindView(R.id.subjectSpinnerLayout)
    LinearLayout subjectSpinnerLayout;
    @BindView(R.id.subjectSpinner)
    Spinner subjectSpinner;

    StudentAttendanceDetailAdapter adapter;

    boolean bool;

    String enrollmentId;

    List<String> idList = new ArrayList<>();
    FragmentAttendanceStudentBinding binding;

    GetAttendanceMonthResponse monthResponse;


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_student, container, false);
        binding = FragmentAttendanceStudentBinding.inflate(inflater, container, false);
        ButterKnife.bind(this, view);


        enrollmentId = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("enrollmentId");
        if (enrollmentId == null) {
            enrollmentId = Preferences.getInstance(getContext()).getEnrollmentId();
        }
        monthResponse=Preferences.getInstance(getContext()).getMonthResponse();

        monthSpinner.setSelection(Helper.getCurrentMonth() - 1);

        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getContext(), monthResponse.getAttendanceMonthResponses());
        monthSpinner.setAdapter(spinnerAdapter);
        monthSpinner.setSelection(monthResponse.getCurrentMonthIndex());

        if (ParameterConstant.isAttendanceModeSubjectWise(getContext())) {
            subjectSpinnerLayout.setVisibility(View.VISIBLE);
        } else {
            subjectSpinnerLayout.setVisibility(View.GONE);
        }

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ParameterConstant.isAttendanceModeSubjectWise(getContext())) {

                    apiCallBackWithout(getApiCommonController().getSubjectList(ParameterConstant.getRole(getContext()),
                            Objects.requireNonNull(getActivity()).getIntent().getStringExtra("classId"),
                            getActivity().getIntent().getStringExtra("sectionId")));


                } else {
                    attendanceApi();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bool) {
                    attendanceApi();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }


    @SuppressLint("NotifyDataSetChanged")
    void attendanceApi() {
        if (adapter!=null){
            adapter.responseList.getAttendanceResponses().clear();
            adapter.notifyDataSetChanged();
        }
        bool = false;
        apiCallBackWithout(getApiCommonController().getAttendanceStudent(ParameterConstant.getRole(getContext()),
                monthResponse.getAttendanceMonthResponses().get(monthSpinner.getSelectedItemPosition()).getMonth(),
                monthResponse.getAttendanceMonthResponses().get(monthSpinner.getSelectedItemPosition()).getYear(),
                enrollmentId));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    @Subscribe
    public void getAttendance(GetStudentAttendanceDetailResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            this.response = response;
            bool = true;
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            adapter=new StudentAttendanceDetailAdapter(getActivity(), response.getStudentAttendanceDetailWrapperResponse().getStudentAttendanceDetailResponse());
            recyclerView.setAdapter(adapter);
        } else {
            dialogError(response.getMessage().getMessage());
        }

    }


    @Subscribe
    public void getSubject(GetStandaloneSubjectResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            List<String> list = new ArrayList<>();
            idList.clear();
            for (int i = 0; i < response.getStandaloneSubjectResponses().size(); i++) {
                for (int j = 0; j < response.getStandaloneSubjectResponses().get(i).getStandaloneSubjects().size(); j++) {
                    list.add(response.getStandaloneSubjectResponses().get(i).getStandaloneSubjects().get(j).getName());
                    idList.add(response.getStandaloneSubjectResponses().get(i).getStandaloneSubjects().get(j).getId());
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subjectSpinner.setAdapter(adapter);


            attendanceApi();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

}
