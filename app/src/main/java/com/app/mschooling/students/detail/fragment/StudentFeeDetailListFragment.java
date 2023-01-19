package com.app.mschooling.students.detail.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.students.detail.adapter.StudentFeeDetailFragmentListAdapter;
import com.app.mschooling.utils.AppUser;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.student.request.StudentFeeRequest;
import com.mschooling.transaction.common.student.response.StudentFeeResponse;
import com.mschooling.transaction.filter.FeeDetailCriteria;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.fee.GetStudentFeeDetailResponse;
import com.mschooling.transaction.response.student.GetStudentDetailResponse;
import com.mschooling.transaction.response.student.UpdateStudentResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentFeeDetailListFragment extends BaseFragment {

    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.totalFee)
    TextView totalFee;
    @BindView(R.id.totalPaid)
    TextView totalPaid;
    @BindView(R.id.totalDue)
    TextView totalDue;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.submit)
    Button submit;


    StudentFeeDetailFragmentListAdapter adapter;
    String intent;


    String enrollmentId;
    StudentFeeResponse response;
    boolean update ;
    
    public StudentFeeDetailListFragment(GetStudentDetailResponse response,boolean update) {
        this.response = response.getStudentDetailResponse().getStudentFeeResponse();
        enrollmentId = response.getStudentDetailResponse().getStudentBasicResponse().getEnrollmentId();
        this.update=update;
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_fee_detail_list, container, false);
        ButterKnife.bind(this, view);
        intent = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("intent");


        updateView();
        if (update) {
            submit.setVisibility(View.VISIBLE);
        } else {
            submit.setVisibility(View.GONE);
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentFeeRequest request=new StudentFeeRequest();
                request.setStudentFees(response.getStudentFees());
                UpdateStudentRequest mRequest=new UpdateStudentRequest();
                mRequest.getStudentBasicRequest().setEnrollmentId(enrollmentId);
                mRequest.setStudentFeeRequest(request);
                apiCallBack(getApiCommonController().updateStudent(mRequest));
            }
        });
        return view;

    }

    void updateView(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        adapter = new StudentFeeDetailFragmentListAdapter(getActivity(), intent, response.getStudentFees(),update);
        recyclerView.setAdapter(adapter);
        totalFee.setText(String.valueOf(response.getSumTotalFee()));
        totalDue.setText(String.valueOf((response.getSumTotalFee()-response.getSumPaidFee())));
        totalPaid.setText(String.valueOf(response.getSumPaidFee()));
    }


    void getFee(){
        FeeDetailCriteria feeDetailCriteria = new FeeDetailCriteria();
        feeDetailCriteria.setEnrollmentId(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("enrollmentId"));
        apiCallBack(getApiCommonController().getStudentFee(feeDetailCriteria));
    }

    @Subscribe
    public void getFeeList(GetStudentFeeDetailResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response.setStudentFees(response.getStudentFees());
            updateView();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void update(UpdateStudentResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            getFee();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


}
