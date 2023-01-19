package com.app.mschooling.homework.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.RowHomeWorkStatusListBinding;
import com.app.mschooling.event_handler.CommonEvent;
import com.app.mschooling.event_handler.EventBasedOnType;
import com.app.mschooling.students.detail.activity.StudentBasicDetailActivity;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.student.response.StudentBasicResponse;
import com.mschooling.transaction.common.student.response.StudentPersonalResponse;
import com.mschooling.transaction.common.student.response.StudentProfileResponse;
import com.mschooling.transaction.response.homework.StudentWorksheetResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class HomeWorkStatusListAdapter extends RecyclerView.Adapter<HomeWorkStatusListAdapter.ViewHolder> {

    private Activity context;
    public List<StudentWorksheetResponse> responseList;


    public HomeWorkStatusListAdapter(Activity context, List<StudentWorksheetResponse> data) {

        this.context = context;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RowHomeWorkStatusListBinding binding = RowHomeWorkStatusListBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new ViewHolder(binding.getRoot());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        StudentBasicResponse basicResponse = responseList.get(position).getStudentBasicResponse();
        StudentProfileResponse profileResponse = responseList.get(position).getStudentProfileResponse();
        StudentPersonalResponse personalResponse = responseList.get(position).getStudentPersonalResponse();
        viewHolder.binding.name.setText(Helper.getName(basicResponse.getfName(), basicResponse.getlName()));
        viewHolder.binding.fatherName.setText(Helper.getFatherName(personalResponse.getfName()));
        viewHolder.binding.enrolmentId.setText(basicResponse.getEnrollmentId());
        Helper.getProfileImage(context, profileResponse.getProfileFirebase(), viewHolder.binding.image, viewHolder.binding.shimmerFrameLayout);
        viewHolder.binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StudentBasicDetailActivity.class);
                intent.putExtra("id", basicResponse.getEnrollmentId());
                context.startActivity(intent);
            }
        });
        viewHolder.binding.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new CommonEvent( position));
            }
        });
        viewHolder.binding.acceptLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventBasedOnType(context.getString(R.string.accept), Common.WorksheetStatus.APPROVED, position));

            }
        });
        viewHolder.binding.rejectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventBasedOnType(context.getString(R.string.reject), Common.WorksheetStatus.REJECTED, position));
            }
        });

        if (responseList.get(position).getWorksheetStatus() == Common.WorksheetStatus.SUBMITTED) {
            viewHolder.binding.status.setVisibility(View.GONE);
            viewHolder.binding.actionLayout.setVisibility(View.VISIBLE);
            viewHolder.binding.status.setText("");
            viewHolder.binding.actionLayout.setVisibility(View.VISIBLE);
        }else if (responseList.get(position).getWorksheetStatus() == Common.WorksheetStatus.NOT_SUBMITTED) {
            viewHolder.binding.status.setVisibility(View.VISIBLE);
            viewHolder.binding.actionLayout.setVisibility(View.GONE);
            viewHolder.binding.status.setText(context.getString(R.string.not_submitted_yet));
            viewHolder.binding.accept.setTextColor(context.getResources().getColor(R.color.red));
        } else if (responseList.get(position).getWorksheetStatus() == Common.WorksheetStatus.APPROVED) {
            viewHolder.binding.status.setText(context.getString(R.string.accepted));
            viewHolder.binding.status.setVisibility(View.VISIBLE);
            viewHolder.binding.actionLayout.setVisibility(View.GONE);
            viewHolder.binding.acceptLayout.setBackground(context.getResources().getDrawable(R.drawable.view_border2));
            viewHolder.binding.accept.setTextColor(context.getResources().getColor(R.color.military_blue));
            viewHolder.binding.rejectLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
            viewHolder.binding.reject.setTextColor(context.getResources().getColor(R.color.default_text));
        } else if (responseList.get(position).getWorksheetStatus() == Common.WorksheetStatus.REJECTED) {
            viewHolder.binding.status.setText(context.getString(R.string.rejected));
            viewHolder.binding.status.setVisibility(View.VISIBLE);
            viewHolder.binding.actionLayout.setVisibility(View.GONE);
            viewHolder.binding.acceptLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
            viewHolder.binding.accept.setTextColor(context.getResources().getColor(R.color.default_text));
            viewHolder.binding.rejectLayout.setBackground(context.getResources().getDrawable(R.drawable.view_border2));
            viewHolder.binding.reject.setTextColor(context.getResources().getColor(R.color.military_blue));
        }

    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        RowHomeWorkStatusListBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.findBinding(itemView.getRootView());
        }
    }


}