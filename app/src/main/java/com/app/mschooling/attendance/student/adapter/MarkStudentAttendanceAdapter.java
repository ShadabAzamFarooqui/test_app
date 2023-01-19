package com.app.mschooling.attendance.student.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.app.mschooling.attendance.student.activity.StudentAttendanceDetailActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.RowAttendanceBinding;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.attendance.AttendanceResponse;
import com.mschooling.transaction.response.attendance.StudentAttendanceResponse;

import java.util.ArrayList;
import java.util.List;

public class MarkStudentAttendanceAdapter extends RecyclerView.Adapter<MarkStudentAttendanceAdapter.ViewHolder> implements Filterable {

    private final Activity context;
    public List<StudentAttendanceResponse> filteredList;
    public List<StudentAttendanceResponse> responseList;


    public MarkStudentAttendanceAdapter(Activity context, List<StudentAttendanceResponse> responseList) {

        this.context = context;
        this.responseList = responseList;
        this.filteredList = responseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RowAttendanceBinding binding = RowAttendanceBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new MarkStudentAttendanceAdapter.ViewHolder(binding.getRoot());
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "NotifyDataSetChanged"})
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        AttendanceResponse attendanceResponse = responseList.get(position).getAttendanceResponse();
        viewHolder.binding.name.setText(Helper.getName(responseList.get(position).getfName(),responseList.get(position).getlName()));
        viewHolder.binding.fatherName.setText(responseList.get(position).getFather()!=null?responseList.get(position).getFather():context.getString(R.string.not_found));
        viewHolder.binding.enrolmentId.setText(responseList.get(position).getEnrollmentId());

        String first = String.valueOf(responseList.get(position).getfName().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(first);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(50)
                .height(50)
                .endConfig()
                .buildRoundRect(first, color, 10);
        viewHolder.binding.image.setImageDrawable(drawable);

        try {
            if (attendanceResponse.getAttendance() == null) {
                viewHolder.binding.presentLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
                viewHolder.binding.present.setTextColor(context.getResources().getColor(R.color.default_text));
                viewHolder.binding.absentLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
                viewHolder.binding.absent.setTextColor(context.getResources().getColor(R.color.default_text));
                viewHolder.binding.leaveLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
                viewHolder.binding.leave.setTextColor(context.getResources().getColor(R.color.default_text));
            } else if (attendanceResponse.getAttendance().value().equals("P")) {
                viewHolder.binding.presentLayout.setBackground(context.getResources().getDrawable(R.drawable.view_border2));
                viewHolder.binding.present.setTextColor(context.getResources().getColor(R.color.military_blue));
                viewHolder.binding.absentLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
                viewHolder.binding.absent.setTextColor(context.getResources().getColor(R.color.default_text));
                viewHolder.binding.leaveLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
                viewHolder.binding.leave.setTextColor(context.getResources().getColor(R.color.default_text));
            } else if (attendanceResponse.getAttendance().value().equals("A")) {
                viewHolder.binding.absentLayout.setBackground(context.getResources().getDrawable(R.drawable.view_border2));
                viewHolder.binding.absent.setTextColor(context.getResources().getColor(R.color.military_blue));
                viewHolder.binding.presentLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
                viewHolder.binding.present.setTextColor(context.getResources().getColor(R.color.default_text));
                viewHolder.binding.leaveLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
                viewHolder.binding.leave.setTextColor(context.getResources().getColor(R.color.default_text));
            } else if (attendanceResponse.getAttendance().value().equals("L")) {
                viewHolder.binding.leaveLayout.setBackground(context.getResources().getDrawable(R.drawable.view_border2));
                viewHolder.binding.leave.setTextColor(context.getResources().getColor(R.color.military_blue));
                viewHolder.binding.absentLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
                viewHolder.binding.absent.setTextColor(context.getResources().getColor(R.color.default_text));
                viewHolder.binding.presentLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
                viewHolder.binding.present.setTextColor(context.getResources().getColor(R.color.default_text));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        viewHolder.binding.presentLayout.setOnClickListener(view -> {
            attendanceResponse.setAttendance(Common.Attendance.P);
            notifyDataSetChanged();

        });

        viewHolder.binding.absentLayout.setOnClickListener(view -> {
            attendanceResponse.setAttendance(Common.Attendance.A);
            notifyDataSetChanged();

        });
        viewHolder.binding.leaveLayout.setOnClickListener(view -> {
            attendanceResponse.setAttendance(Common.Attendance.L);
            notifyDataSetChanged();

        });


        viewHolder.binding.clear.setOnClickListener(view -> {
            attendanceResponse.setAttendance(null);
            notifyDataSetChanged();
        });

        viewHolder.binding.mainLayout.setOnClickListener(view -> {
            Intent intent=new Intent(context, StudentAttendanceDetailActivity.class);
            intent.putExtra("enrollmentId",responseList.get(position).getEnrollmentId());
            intent.putExtra("name", Helper.getName(responseList.get(position).getfName(),responseList.get(position).getlName()));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        RowAttendanceBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.findBinding(itemView.getRootView());
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = responseList;
                } else {
                    ArrayList<StudentAttendanceResponse> filteredList = new ArrayList<>();
                    for (StudentAttendanceResponse wrapper : responseList) {
                        if (wrapper.getlName() == null) {
                            wrapper.setlName("");
                        }
                        if (wrapper.getfName().toLowerCase().startsWith(charString) || charString.startsWith(wrapper.getlName())) {
                            filteredList.add(wrapper);
                        } else if (wrapper.getEnrollmentId().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(wrapper);
                        }
                    }
                    MarkStudentAttendanceAdapter.this.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (List<StudentAttendanceResponse>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }
}